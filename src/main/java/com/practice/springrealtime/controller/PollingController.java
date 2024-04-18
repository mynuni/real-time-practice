package com.practice.springrealtime.controller;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.dto.MessageRequest;
import com.practice.springrealtime.dto.MessageResponse;
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
    public ResponseEntity<List<MessageResponse>> getMessages() {
        // TODO: "ABC" needs to be replaced with the session id
        List<MessageResponse> messages = pollingService.getMessages("ABC");
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @PostMapping
    public ResponseEntity<Void> saveMessage(@RequestBody MessageRequest messageRequest) {
        pollingService.saveMessage(messageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
