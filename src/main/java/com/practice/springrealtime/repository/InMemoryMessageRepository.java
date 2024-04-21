package com.practice.springrealtime.repository;

import com.practice.springrealtime.domain.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryMessageRepository implements MessageRepository {

    // 세션 ID별 마지막으로 읽은 메시지 인덱스
    private final Map<String, Long> lastReadMessage = new ConcurrentHashMap<>();
    private final List<Message> messageStorage = new CopyOnWriteArrayList<>();

    @Override
    public void saveMessage(Message message) {
        messageStorage.add(message);
    }

    @Override
    public List<Message> getMessages(String sessionId) {
        Long lastReadMessageIndex = lastReadMessage.getOrDefault(sessionId, -1L);

        // 마지막으로 읽은 메시지 이후의 메시지들을 가져옴
        List<Message> unreadMessages = messageStorage.subList(lastReadMessageIndex.intValue() + 1, messageStorage.size());

        // 읽음 처리
        lastReadMessage.put(sessionId, (long) messageStorage.size() - 1);
        return unreadMessages;
    }

    @Override
    public Message getMessage(int index) {
        return messageStorage.get(index);
    }

}
