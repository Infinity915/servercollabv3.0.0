package com.studencollabfin.server.controller;

import com.studencollabfin.server.model.Application;
import com.studencollabfin.server.model.BuddyBeacon;
import com.studencollabfin.server.service.BuddyBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beacon") // Changed to /api/beacon to match the frontend
public class BuddyBeaconController {

    @Autowired
    private BuddyBeaconService beaconService;
    
    // Placeholder for getting the currently logged-in user's ID
    private String getCurrentUserId() {
        return "anonymous-user-id";
    }

    // --- Beacon Post Endpoints ---

    @PostMapping
    public BuddyBeacon createBeaconPost(@RequestBody BuddyBeacon beaconPost) {
        return beaconService.createBeaconPost(getCurrentUserId(), beaconPost);
    }

    @GetMapping
    public List<BuddyBeacon> getAllBeaconPosts() {
        return beaconService.getAllBeaconPosts();
    }

    // --- Application Endpoints ---

    @PostMapping("/{beaconId}/applications")
    public Application applyToBeaconPost(@PathVariable String beaconId, @RequestBody Application application) {
        return beaconService.applyToBeaconPost(beaconId, getCurrentUserId(), application);
    }

    @GetMapping("/{beaconId}/applications")
    public List<Application> getApplicationsForPost(@PathVariable String beaconId) {
        return beaconService.getApplicationsForPost(beaconId, getCurrentUserId());
    }

    @PutMapping("/{beaconId}/applications/{applicationId}")
    public Application updateApplicationStatus(
            @PathVariable String beaconId,
            @PathVariable String applicationId,
            @RequestBody String status) { // Status will be sent as a simple string like "ACCEPTED"
        return beaconService.updateApplicationStatus(beaconId, applicationId, status, getCurrentUserId());
    }
}