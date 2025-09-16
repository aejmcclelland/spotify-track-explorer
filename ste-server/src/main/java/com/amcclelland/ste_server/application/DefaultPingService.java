package com.amcclelland.ste_server.application;

import org.springframework.stereotype.Service;

@Service
public class DefaultPingService implements PingService {
    @Override
    public String ping() {
        return "pong";
    }

}
