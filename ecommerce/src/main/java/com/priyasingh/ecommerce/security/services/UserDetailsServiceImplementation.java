package com.priyasingh.ecommerce.security.services;

import com.priyasingh.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.priyasingh.ecommerce.model.User;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // this ensures the database operation is handled in a transaction - either the entire operation is completed and if there is any sort of error in between then the entire operation is rolledBack.
    public UserDetailsImplementation loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        return UserDetailsImplementation.build(user);
    }
}
