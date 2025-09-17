package com.amcclelland.ste_server.config;

import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
public class UserDetailsConfig {

    @Bean
    UserDetailsService userDetailsService(UserRepository repo) {
        return (String email) -> repo.findByEmail(email)
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getPasswordHash())
                        // u.getRole() contains "ROLE_USER" or "ROLE_ADMIN"
                        .authorities(List.of(new SimpleGrantedAuthority(u.getRole())))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
