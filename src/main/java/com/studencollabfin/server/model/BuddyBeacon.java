package com.studencollabfin.server.model;

import com.fasterxml.jackson.annotation.JsonFormat; // <-- Add this import
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "beacons")
public class BuddyBeacon {
    @Id
    private String id;
    private String authorId;
    private String eventId;
    private String eventName;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private int maxTeamSize;
    private List<String> currentTeamMemberIds;
    private String status;

    // Add this annotation to handle the date format correctly
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}