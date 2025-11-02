package com.priyasingh.ecommerce.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.priyasingh.ecommerce.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class UserDetailsImplementation implements UserDetails {
    private static final long serialVersionUID = 1L; // for serialization in java, like a version control number

    private Long id;
    private String username;
    private String email;

    @JsonIgnore  // this ensures that password is not a part of serializable object
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImplementation(Long id,
                                     String username,
                                     String email,
                                     String password,
                                     Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // to convert our User that we have defined in model to userDetailsImplementation
    public static UserDetailsImplementation build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImplementation(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // not handling this - true by default
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // not handling this - true by default
    }

    @Override
    public boolean isEnabled() {
        return true; // not handling this - true by default
    }

    @Override  // adding ability to compare the user details objects by id
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        UserDetailsImplementation user = (UserDetailsImplementation) o;
        return Objects.equals(id, user.id);
    }
}
