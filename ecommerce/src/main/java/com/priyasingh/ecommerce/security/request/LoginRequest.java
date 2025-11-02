package com.priyasingh.ecommerce.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
    @NotBlank
    @Getter
    @Setter
    private String username;

    @NotBlank
    @Getter
    @Setter
    private String password;
}
