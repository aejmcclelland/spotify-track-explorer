package com.amcclelland.ste_server.application;

import com.amcclelland.ste_server.domain.User;
import com.amcclelland.ste_server.infra.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    

    public DefaultUserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public User register(String email, String rawPassword) {
        if (repo.existsByEmail(email))
            throw new IllegalArgumentException("email already registered");
        var user = new User(email, encoder.encode(rawPassword));
        return repo.save(user);
    }

    @Override
    public User loadByEmail(String email) {
        return repo.findByEmail(email).orElseThrow();
    }
}
