package com.practice.springrealtime.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.springrealtime.domain.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("새로운 연결: {}", session.getId());
        sessionMap.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // 웹 소켓 세션 ID를 발신자로 사용
            String serializedMessage = objectMapper.writeValueAsString(new Message(session.getId(), message.getPayload()));
            TextMessage textMessage = new TextMessage(serializedMessage);

            // 연결된 모든 세션에 메시지 전송
            for (WebSocketSession webSocketSession : sessionMap.values()) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(textMessage);
                }
            }
        } catch (IOException e) {
            log.error("전송 오류: {}", e.getMessage());
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        WebSocketSession remove = sessionMap.remove(session.getId());
        if (remove != null) {
            log.info("연결 해제: {}", remove.getId());
            remove.close();
        }

    }

}
