package com.priyasingh.ecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
public class UserInfoResponse {
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String jwtToken;

    @Getter
    @Setter
    private List<String> roles;
}
