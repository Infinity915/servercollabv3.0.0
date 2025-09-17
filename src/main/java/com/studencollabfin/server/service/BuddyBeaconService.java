package com.studencollabfin.server.service;

import com.studencollabfin.server.model.Application;
import com.studencollabfin.server.model.BuddyBeacon;
import com.studencollabfin.server.repository.ApplicationRepository;
import com.studencollabfin.server.repository.BuddyBeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuddyBeaconService {
    
    @Autowired
    private BuddyBeaconRepository beaconRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // --- Beacon Post Logic ---

    public BuddyBeacon createBeaconPost(String userId, BuddyBeacon beaconPost) {
        beaconPost.setAuthorId(userId);
        beaconPost.setCreatedAt(LocalDateTime.now());
        beaconPost.setStatus("OPEN");
        if (beaconPost.getCurrentTeamMemberIds() == null) {
            beaconPost.setCurrentTeamMemberIds(new ArrayList<>());
        }
        // The author is the first member
        beaconPost.getCurrentTeamMemberIds().add(userId);
        return beaconRepository.save(beaconPost);
    }

    public List<BuddyBeacon> getAllBeaconPosts() {
        return beaconRepository.findAll();
    }

    // --- Application Logic ---

    public Application applyToBeaconPost(String beaconId, String applicantId, Application application) {
        // Ensure the beacon post exists
        beaconRepository.findById(beaconId)
            .orElseThrow(() -> new RuntimeException("Beacon post not found"));
        
        application.setBeaconId(beaconId);
        application.setApplicantId(applicantId);
        application.setCreatedAt(LocalDateTime.now());
        application.setStatus("PENDING");
        
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsForPost(String beaconId, String userId) {
        BuddyBeacon beaconPost = beaconRepository.findById(beaconId)
            .orElseThrow(() -> new RuntimeException("Beacon post not found"));
        
        // Security check: only the author can view applications
        if (!beaconPost.getAuthorId().equals(userId)) {
            throw new RuntimeException("User not authorized to view applications for this post");
        }
        
        return applicationRepository.findByBeaconId(beaconId);
    }

    public Application updateApplicationStatus(String beaconId, String applicationId, String status, String userId) {
        BuddyBeacon beaconPost = beaconRepository.findById(beaconId)
            .orElseThrow(() -> new RuntimeException("Beacon post not found"));
        
        // Security check: only the author can accept/reject
        if (!beaconPost.getAuthorId().equals(userId)) {
            throw new RuntimeException("User not authorized to update applications for this post");
        }

        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);

        // If accepted, add the applicant to the team members list
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            List<String> members = beaconPost.getCurrentTeamMemberIds();
            if (!members.contains(application.getApplicantId())) {
                members.add(application.getApplicantId());
                beaconPost.setCurrentTeamMemberIds(members);
                
                // If team is now full, close the post
                if (members.size() >= beaconPost.getMaxTeamSize()) {
                    beaconPost.setStatus("CLOSED");
                }
                beaconRepository.save(beaconPost);
            }
        }
        
        return applicationRepository.save(application);
    }
}