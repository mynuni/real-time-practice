package com.practice.springrealtime.controller;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.dto.MessageRequest;
import com.practice.springrealtime.dto.MessageResponse;
import com.practice.springrealtime.global.annotation.SessionId;
import com.practice.springrealtime.service.PollingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/messages/polling")
@RequiredArgsConstructor
@RestController
public class PollingController {

    private final PollingService pollingService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(@SessionId String sessionId) {
        List<MessageResponse> messages = pollingService.getMessages(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @PostMapping
    public ResponseEntity<Void> saveMessage(@SessionId String sessionId, @RequestBody MessageRequest messageRequest) {
        pollingService.saveMessage(sessionId, messageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
