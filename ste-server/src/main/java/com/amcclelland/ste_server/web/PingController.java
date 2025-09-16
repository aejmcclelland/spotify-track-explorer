package com.amcclelland.ste_server.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amcclelland.ste_server.application.PingService;

@RestController
public class PingController {
    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping("/api/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(pingService.ping().value());
    }

    @GetMapping("/api/secure/hello")
    public ResponseEntity<String> secureHello() {
        return ResponseEntity.ok("hello, secure world");
    }
}
