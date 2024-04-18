package com.practice.springrealtime.repository;

import com.practice.springrealtime.domain.Message;

import java.util.List;

public interface MessageRepository {

    void saveMessage(Message message);

    List<Message> getMessages(String sessionId);

}
