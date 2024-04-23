package com.practice.springrealtime.global.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.springrealtime.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // GenericMessage의 payload는 byte[]
        String payload = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);

        // 발신자는 인증 토큰에서 확인하도록 할 수 있음, 여기서는 소켓 세션 ID로 대체
        MessageResponse messageResponse = new MessageResponse(headerAccessor.getSessionId(), payload);

        try {
            return MessageBuilder.withPayload(objectMapper.writeValueAsString(messageResponse))
                    .copyHeaders(message.getHeaders())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 전송 실패", e);
        }

    }

}
