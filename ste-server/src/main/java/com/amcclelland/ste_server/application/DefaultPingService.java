package com.amcclelland.ste_server.application;

import org.springframework.stereotype.Service;

import com.amcclelland.ste_server.domain.Message;

@Service
public class DefaultPingService implements PingService {
    @Override
    public Message ping() {
        return new Message("pong");
    }

}
