package com.amcclelland.ste_server.application;

import com.amcclelland.ste_server.domain.User;

public interface UserService {
    User register(String email, String rawPassword);

    User loadByEmail(String email);
}
