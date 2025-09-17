package com.studencollabfin.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) // Important for Lombok to include parent class fields
@Document(collection = "posts")
public class TeamFindingPost extends Post {

    private String eventId; // ID of the event this post is for
    private List<String> requiredSkills;
    private int maxTeamSize;
    private List<String> currentTeamMembers;
    private String status; // e.g., "OPEN", "CLOSED"
}