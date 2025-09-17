package com.studencollabfin.server.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "collabPods")
public class CollabPod {
    @Id
    private String id;
    private String name;
    private String description;
    private String creatorId;
    private List<String> memberIds;
    private List<String> moderatorIds;
    private int maxCapacity;
    private List<String> topics;
    private PodType type; // Changed from 'podType' to 'type' to match frontend
    private PodStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastActive;
    private List<String> resources; // Links to study materials
    private List<Meeting> meetings;
    
    @Data
    public static class Meeting {
        private String id;
        private String title;
        private String description;
        private LocalDateTime scheduledTime;
        private String meetingLink;
        private List<String> attendeeIds;
        private MeetingStatus status;
    }
    
    // This enum now matches the values sent from the frontend
    public enum PodType {
        DISCUSSION,
        ASK,
        HELP,
        PROJECT_TEAM,
        MENTORSHIP,
        COURSE_SPECIFIC
    }
    
    public enum PodStatus {
        ACTIVE,
        FULL,
        ARCHIVED,
        CLOSED
    }
    
    public enum MeetingStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}