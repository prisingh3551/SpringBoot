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

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse(Long id, String username, List<String> roles, String jwtCookie) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtCookie;
    }
}
