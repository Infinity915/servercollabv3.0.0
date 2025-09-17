package com.studencollabfin.server.service;

import com.studencollabfin.server.model.Post;
import com.studencollabfin.server.model.TeamFindingPost;
import com.studencollabfin.server.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    // Poll voting logic
    public Post voteOnPollOption(String postId, int optionId, String userId) {
        Post post = getPostById(postId);
        if (post instanceof com.studencollabfin.server.model.SocialPost) {
            com.studencollabfin.server.model.SocialPost social = (com.studencollabfin.server.model.SocialPost) post;
            java.util.List<com.studencollabfin.server.model.PollOption> options = social.getPollOptions();
            if (options != null && optionId >= 0 && optionId < options.size()) {
                com.studencollabfin.server.model.PollOption option = options.get(optionId);
                if (option.getVotes() == null) {
                    option.setVotes(new java.util.ArrayList<>());
                }
                // Prevent duplicate votes
                boolean alreadyVoted = options.stream().anyMatch(opt -> opt.getVotes() != null && opt.getVotes().contains(userId));
                if (!alreadyVoted) {
                    option.getVotes().add(userId);
                    postRepository.save(social);
                }
            }
        }
        return post;
    }
    @org.springframework.beans.factory.annotation.Autowired
    private com.studencollabfin.server.service.UserService userService;

    public com.studencollabfin.server.model.User getUserById(String userId) {
        return userService.getUserById(userId);
    }

    private final PostRepository postRepository;

    // This method can save any kind of post (SocialPost, TeamFindingPost, etc.)
    public Post createPost(Post post, String authorId) {
        post.setAuthorId(authorId);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Fetch TeamFindingPosts by eventId
    public List<TeamFindingPost> getTeamFindingPostsByEventId(String eventId) {
        return postRepository.findByEventId(eventId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }
}