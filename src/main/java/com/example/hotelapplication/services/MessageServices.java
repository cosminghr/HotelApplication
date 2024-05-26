package com.example.hotelapplication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageServices {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendProductNotification(String message) {
        messagingTemplate.convertAndSend("/topic/products", message);
    }
}