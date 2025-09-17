package com.studencollabfin.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "posts")
public class SocialPost extends Post {

    private String title; // Title of the post
    private List<String> likes; // A list of user IDs who liked the post
    private List<Comment> comments; // A list of comment objects
    private List<PollOption> pollOptions; // Options for poll posts
    private String postType; // Type of the social post (poll, ask, help)

    // This defines the Comment class, fixing the error.
    @Data
    public static class Comment {
        private String authorId;
        private String text;
        private LocalDateTime commentedAt;
    }
}