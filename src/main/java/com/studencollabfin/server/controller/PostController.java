package com.studencollabfin.server.controller;

import com.studencollabfin.server.model.Post;
import com.studencollabfin.server.model.SocialPost;
import com.studencollabfin.server.model.TeamFindingPost;
import com.studencollabfin.server.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    // Poll voting endpoint
    @PutMapping("/{postId}/vote/{optionId}")
    public ResponseEntity<Post> voteOnPollOption(@PathVariable String postId, @PathVariable int optionId) {
        // Simulate current user ID (replace with real user ID in production)
        String userId = getCurrentUserId();
        Post updatedPost = postService.voteOnPollOption(postId, optionId, userId);
        return ResponseEntity.ok(updatedPost);
    }

    private final PostService postService;

    // A placeholder for getting the current user's ID
    private String getCurrentUserId() {
        // In a real application, you'd get this from the security context
        return "placeholder-user-id";
    }

    @GetMapping
        public ResponseEntity<List<Object>> getAllPosts() {
            List<Post> posts = postService.getAllPosts();
            List<Object> richPosts = new java.util.ArrayList<>();
            for (Post post : posts) {
                java.util.Map<String, Object> richPost = new java.util.HashMap<>();
                richPost.put("id", post.getId());
                richPost.put("authorId", post.getAuthorId());
                richPost.put("createdAt", post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
                if (post instanceof com.studencollabfin.server.model.SocialPost) {
                    com.studencollabfin.server.model.SocialPost social = (com.studencollabfin.server.model.SocialPost) post;
                    richPost.put("title", social.getTitle() != null ? social.getTitle() : "");
                    richPost.put("content", social.getContent());
                    richPost.put("postType", social.getPostType());
                    richPost.put("likes", social.getLikes() != null ? social.getLikes() : new java.util.ArrayList<>());
                    richPost.put("comments", social.getComments() != null ? social.getComments() : new java.util.ArrayList<>());
                    richPost.put("pollOptions", social.getPollOptions() != null ? social.getPollOptions() : new java.util.ArrayList<>());
                } else if (post instanceof com.studencollabfin.server.model.TeamFindingPost) {
                    com.studencollabfin.server.model.TeamFindingPost team = (com.studencollabfin.server.model.TeamFindingPost) post;
                    richPost.put("title", team.getContent());
                    richPost.put("content", team.getContent());
                    richPost.put("postType", "team-finding");
                    richPost.put("requiredSkills", team.getRequiredSkills() != null ? team.getRequiredSkills() : new java.util.ArrayList<>());
                    richPost.put("maxTeamSize", team.getMaxTeamSize());
                    richPost.put("currentTeamMembers", team.getCurrentTeamMembers() != null ? team.getCurrentTeamMembers() : new java.util.ArrayList<>());
                } else {
                    richPost.put("title", post.getContent());
                    richPost.put("content", post.getContent());
                    richPost.put("postType", "post");
                }
                richPosts.add(richPost);
            }
            return ResponseEntity.ok(richPosts);
        }

    @PostMapping("/social")
    public ResponseEntity<Post> createSocialPost(@RequestBody SocialPost socialPost) {
        Post createdPost = postService.createPost(socialPost, getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/team-finding")
    public ResponseEntity<Post> createTeamFindingPost(@RequestBody TeamFindingPost teamFindingPost) {
        Post createdPost = postService.createPost(teamFindingPost, getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // Fetch TeamFindingPosts by eventId
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Object>> getTeamFindingPostsByEventId(@PathVariable String eventId) {
        List<TeamFindingPost> posts = postService.getTeamFindingPostsByEventId(eventId);
        List<Object> richPosts = new java.util.ArrayList<>();
        for (TeamFindingPost post : posts) {
            // Fetch author details (simulate, replace with actual user fetch)
            com.studencollabfin.server.model.User author = null;
            try {
                author = postService.getUserById(post.getAuthorId());
            } catch (Exception e) {
                // fallback if user not found
            }
            java.util.Map<String, Object> authorObj = new java.util.HashMap<>();
            if (author != null) {
                authorObj.put("id", author.getId());
                authorObj.put("name", author.getFullName());
                authorObj.put("collegeName", author.getCollegeName());
                authorObj.put("year", author.getYearOfStudy());
                authorObj.put("tags", author.getExcitingTags() != null ? author.getExcitingTags() : new java.util.ArrayList<>());
            } else {
                authorObj.put("id", post.getAuthorId());
                authorObj.put("name", "Unknown");
                authorObj.put("collegeName", "Unknown");
                authorObj.put("year", "");
                authorObj.put("tags", new java.util.ArrayList<>());
            }
            java.util.Map<String, Object> richPost = new java.util.HashMap<>();
            richPost.put("id", post.getId());
            richPost.put("author", authorObj);
            richPost.put("eventName", post.getEventId()); // You may want to fetch event name
            richPost.put("title", post.getContent());
            richPost.put("description", post.getContent());
            richPost.put("requiredSkills", post.getRequiredSkills() != null ? post.getRequiredSkills() : new java.util.ArrayList<>());
            richPost.put("maxTeamSize", post.getMaxTeamSize());
            richPost.put("currentTeamMembers", post.getCurrentTeamMembers() != null ? post.getCurrentTeamMembers() : new java.util.ArrayList<>());
            richPost.put("createdAt", post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
            richPost.put("expiresAt", post.getCreatedAt() != null ? post.getCreatedAt().plusHours(48).toString() : "");
            richPost.put("applicants", new java.util.ArrayList<>()); // Add applicants if available
            richPosts.add(richPost);
        }
        return ResponseEntity.ok(richPosts);
    }
}