package com.studencollabfin.server.service;

import com.studencollabfin.server.model.User;
import com.studencollabfin.server.model.Achievement;
import com.studencollabfin.server.repository.UserRepository;
import com.studencollabfin.server.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // XP constants
    private static final int XP_PER_POST = 10;
    private static final int XP_PER_COMMENT = 5;
    private static final int XP_PER_EVENT_CREATED = 20;
    private static final int XP_PER_EVENT_ATTENDED = 15;
    private static final int XP_PER_PROJECT_COMPLETED = 50;
    private static final int XP_PER_COLLAB_POD_CREATED = 25;
    
    public void awardXP(String userId, int xpAmount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        int newXP = user.getXp() + xpAmount;
        int currentLevel = user.getLevel();
        
        // Check if user should level up
        while (newXP >= user.getTotalXP()) {
            currentLevel++;
            newXP -= user.getTotalXP();
            // Increase XP requirement for next level
            user.setTotalXP((int)(user.getTotalXP() * 1.5));
        }
        
        user.setXp(newXP);
        user.setLevel(currentLevel);
        userRepository.save(user);
        
        // Check for achievements
        checkAndAwardAchievements(user);
    }
    
    public User updateUserProfile(String userId, User profileData) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only the allowed fields
        existingUser.setFullName(profileData.getFullName());
        existingUser.setCollegeName(profileData.getCollegeName());
        existingUser.setYearOfStudy(profileData.getYearOfStudy());
        existingUser.setDepartment(profileData.getDepartment());
        existingUser.setSkills(profileData.getSkills());
        existingUser.setRolesOpenTo(profileData.getRolesOpenTo());
        existingUser.setGoals(profileData.getGoals());
        existingUser.setExcitingTags(profileData.getExcitingTags());
        existingUser.setLinkedinUrl(profileData.getLinkedinUrl());
        existingUser.setGithubUrl(profileData.getGithubUrl());
        existingUser.setPortfolioUrl(profileData.getPortfolioUrl());
        existingUser.setProfileCompleted(true);

        return userRepository.save(existingUser);
    }

    public List<Achievement> getUserAchievements(String userId) {
        return achievementRepository.findByUserId(userId);
    }

    public void checkAndAwardAchievements(User user) {
        List<Achievement> possibleAchievements = achievementRepository.findByUserIdAndIsUnlockedTrue(user.getId());
        
        for (Achievement achievement : possibleAchievements) {
            if (!achievement.isUnlocked() && checkAchievementRequirements(user, achievement)) {
                achievement.setUnlocked(true);
                achievement.setUnlockedAt(LocalDateTime.now());
                achievementRepository.save(achievement);
                
                // Award XP for unlocking achievement
                awardXP(user.getId(), achievement.getXpValue());
            }
        }
    }
    
    private boolean checkAchievementRequirements(User user, Achievement achievement) {
        // Implement achievement requirement checking logic here
        return false; // Placeholder
    }
    
    // XP award methods for different actions
    public void awardPostXP(String userId) {
        awardXP(userId, XP_PER_POST);
    }
    
    public void awardCommentXP(String userId) {
        awardXP(userId, XP_PER_COMMENT);
    }
    
    public void awardEventCreationXP(String userId) {
        awardXP(userId, XP_PER_EVENT_CREATED);
    }
    
    public void awardEventAttendanceXP(String userId) {
        awardXP(userId, XP_PER_EVENT_ATTENDED);
    }
    
    public void awardProjectCompletionXP(String userId) {
        awardXP(userId, XP_PER_PROJECT_COMPLETED);
    }
    
    public void awardCollabPodCreationXP(String userId) {
        awardXP(userId, XP_PER_COLLAB_POD_CREATED);
    }

    public User findOrCreateUserByOauth(String oauthId, String name, String pictureUrl, String email) {
        // Find an existing user by their unique LinkedIn ID
        return userRepository.findByOauthId(oauthId)
            .map(existingUser -> {
                // If user exists, update their name and picture from LinkedIn, as it might have changed
                existingUser.setFullName(name);
                existingUser.setProfilePicUrl(pictureUrl);
                return userRepository.save(existingUser);
            })
            .orElseGet(() -> {
                // If user does not exist, check if an account with that email already exists
                Optional<User> userByEmail = userRepository.findByEmail(email);
                if (userByEmail.isPresent()) {
                    // An account with this email exists. Link it to the LinkedIn account.
                    User existingUser = userByEmail.get();
                    existingUser.setOauthId(oauthId);
                    existingUser.setFullName(name);
                    existingUser.setProfilePicUrl(pictureUrl);
                    return userRepository.save(existingUser);
                } else {
                    // No account exists, create a brand new one
                    User newUser = new User();
                    newUser.setOauthId(oauthId);
                    newUser.setFullName(name);
                    newUser.setProfilePicUrl(pictureUrl);
                    newUser.setEmail(email);
                    newUser.setLevel(1);
                    newUser.setXp(0);
                    newUser.setTotalXP(100);
                    newUser.setProfileCompleted(false);
                    return userRepository.save(newUser);
                }
            });
    }

}