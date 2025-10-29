package com.example.securitydemo.jwt;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
