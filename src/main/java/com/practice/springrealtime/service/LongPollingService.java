package com.practice.springrealtime.service;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.dto.MessageResponse;
import com.practice.springrealtime.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class LongPollingService {

    private final List<DeferredResult<MessageResponse>> deferredResults = new CopyOnWriteArrayList<>();
    private final MessageRepository messageRepository;
    private static final long TIMEOUT = 1_000 * 60 * 2L; // 2분

    public List<MessageResponse> getUnreadMessages(String sessionId) {
        return messageRepository.getMessages(sessionId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    public DeferredResult<MessageResponse> subscribe(String sessionId) {
        DeferredResult<MessageResponse> deferredResult = new DeferredResult<>(TIMEOUT);
        deferredResults.add(deferredResult);
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timeout"));
            log.info("Session id {} is timeout", sessionId);
            deferredResults.remove(deferredResult);
        });
        return deferredResult;
    }

    public void sendMessage(Message message) {
        messageRepository.saveMessage(message);
        deferredResults.forEach(deferredResult -> deferredResult.setResult(MessageResponse.from(message))); // 응답이 나감
        deferredResults.clear(); // 완료된 DeferredResult 제거
    }

}
