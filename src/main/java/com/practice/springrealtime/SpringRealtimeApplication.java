package com.practice.springrealtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableWebSocketMessageBroker
@EnableWebSocket
@EnableScheduling
@SpringBootApplication
public class SpringRealtimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRealtimeApplication.class, args);
    }

}
