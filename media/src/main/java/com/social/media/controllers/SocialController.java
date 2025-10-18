package com.social.media.controllers;

import com.social.media.models.SocialUser;
import com.social.media.services.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SocialController {
    @Autowired
    private SocialService socialService;

    @GetMapping("/social/users")
    public ResponseEntity<List<SocialUser>> getAllUsers() {
        List<SocialUser> allUsers = socialService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/social/users")
    public ResponseEntity<SocialUser> addUser(@RequestBody SocialUser socialUser) {
        return new ResponseEntity<>(socialService.addUser(socialUser), HttpStatus.CREATED);
    }
}
