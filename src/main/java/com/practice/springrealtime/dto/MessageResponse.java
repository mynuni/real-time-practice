package com.practice.springrealtime.dto;

import com.practice.springrealtime.domain.Message;

public record MessageResponse(String sender, String message) {

    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getSender(), message.getMessage());
    }

}
