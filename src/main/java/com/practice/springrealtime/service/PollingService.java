package com.practice.springrealtime.service;

import com.practice.springrealtime.domain.Message;
import com.practice.springrealtime.dto.MessageRequest;
import com.practice.springrealtime.dto.MessageResponse;
import com.practice.springrealtime.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PollingService {

    private final MessageRepository messageRepository;

    public List<MessageResponse> getMessages(String sessionId) {
        return messageRepository.getMessages(sessionId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    public void saveMessage(MessageRequest messageRequest) {
        // TODO: "ABC" needs to be replaced with the session id
        Message message = new Message("ABC", messageRequest.message());
        messageRepository.saveMessage(message);
    }

}
