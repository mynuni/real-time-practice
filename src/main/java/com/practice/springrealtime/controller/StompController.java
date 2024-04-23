package com.practice.springrealtime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StompController {

    private final SimpMessageSendingOperations simpleMessageSendingOperations;

    @MessageMapping("/chat")
    public void chat(String message) {
        simpleMessageSendingOperations.convertAndSend("/sub/chat", message);
    }

}
