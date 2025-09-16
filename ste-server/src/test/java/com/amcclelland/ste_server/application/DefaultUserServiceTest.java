package com.amcclelland.ste_server.application;

import com.amcclelland.ste_server.domain.User;
import com.amcclelland.ste_server.infra.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultUserServiceTest {
    @Test
    void registersUser() {
        var repo = Mockito.mock(UserRepository.class);
        var enc = new BCryptPasswordEncoder();
        Mockito.when(repo.existsByEmail("a@b.com")).thenReturn(false);
        Mockito.when(repo.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var svc = new DefaultUserService(repo, enc);
        var u = svc.register("a@b.com", "pass");

        assertThat(u.getEmail()).isEqualTo("a@b.com");
        assertThat(enc.matches("pass", u.getPasswordHash())).isTrue();
    }
}
