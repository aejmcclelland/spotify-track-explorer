package com.amcclelland.ste_server.web;

import com.amcclelland.ste_server.application.PingService;
import com.amcclelland.ste_server.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PingControllerTest {

    private MockMvc mvc;
    private PingService pingService;

    @BeforeEach
    void setup() {
        pingService = Mockito.mock(PingService.class);
        PingController pingController = new PingController(pingService);
        mvc = MockMvcBuilders.standaloneSetup(pingController).build();
    }

    @Test
    void returnsPong() throws Exception {
        when(pingService.ping()).thenReturn(new Message("pong"));
        mvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
