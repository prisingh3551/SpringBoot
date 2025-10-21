package com.social.media.services;

import com.social.media.models.SocialUser;
import com.social.media.repositories.SocialUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialService {
    @Autowired
    private SocialUserRepository socialUserRepository;
    public List<SocialUser> getAllUsers() {
        return socialUserRepository.findAll();
    }

    public SocialUser addUser(SocialUser socialUser) {
        return socialUserRepository.save(socialUser);
    }

    public SocialUser deleteUser(Long userId) {
        SocialUser socialUser = socialUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        socialUserRepository.delete(socialUser);
        return socialUser;
    }
}
