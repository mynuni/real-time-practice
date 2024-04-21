package com.practice.springrealtime.service;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
@Service
public class SseService {

    private final MessageRepository messageRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private static final long SSE_CONNECTION_TIMEOUT = 1000 * 60 * 2L;
    private static final long SSE_SCHEDULED_RATE = 1_000L;
    private static int eventSequence = 0;

    /**
     * 연결 시 메시지를 전송해주어야 503 에러가 발생하지 않음
     * 지금은 스케줄러가 동작하여 메시지를 주기적으로 전송해주므로 생략
     */
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(SSE_CONNECTION_TIMEOUT);
        emitters.add(emitter);

        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError((e) -> {
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
        });

        return emitter;
    }

    private void produceEvent(SseEmitter emitter, int eventSequence, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(eventSequence))
                    .data(data));
        } catch (IOException | IllegalStateException e) {
            emitters.remove(emitter);
        }
    }

    @Scheduled(fixedRate = SSE_SCHEDULED_RATE)
    public void produceScheduledEvent() {
        Message message = new Message("SERVER", UUID.randomUUID().toString());
        messageRepository.saveMessage(message);

        for (SseEmitter emitter : emitters) {
            produceEvent(emitter, eventSequence, message);
        }

        eventSequence++;
    }

}
