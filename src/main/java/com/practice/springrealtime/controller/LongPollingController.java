package com.practice.springrealtime.controller;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.dto.MessageRequest;
import com.practice.springrealtime.dto.MessageResponse;
import com.practice.springrealtime.global.annotation.SessionId;
import com.practice.springrealtime.service.LongPollingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RequestMapping("/api/messages/long-polling")
@RequiredArgsConstructor
@RestController
public class LongPollingController {

    private final LongPollingService longPollingService;

    // 최초 입장 시 읽지 않은 메시지들을 가져옴
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(@SessionId String sessionId) {
        List<MessageResponse> messages = longPollingService.getUnreadMessages(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/subscribe")
    public DeferredResult<MessageResponse> subscribe(@SessionId String sessionId) {
        return longPollingService.subscribe(sessionId);
    }

    @PostMapping
    public ResponseEntity<Void> saveMessage(@SessionId String sessionId, @RequestBody MessageRequest messageRequest) {
        Message message = new Message(sessionId, messageRequest.message());
        longPollingService.sendMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
