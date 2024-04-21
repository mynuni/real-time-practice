package com.practice.springrealtime.controller;

import com.practice.springrealtime.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/api/messages/sse")
@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    /**
     * @param lastEventId 마지막으로 수신한 이벤트 ID
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestHeader(value = "Last-Event-ID", required = false) Integer lastEventId) {
        SseEmitter emitter = sseService.connect(lastEventId);
        return ResponseEntity.ok(emitter);
    }

}
