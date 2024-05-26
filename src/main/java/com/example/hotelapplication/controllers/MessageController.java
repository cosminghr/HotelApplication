package com.example.hotelapplication.controllers;


import com.example.hotelapplication.dtos.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;

@Controller
@CrossOrigin("http://localhost:3000")
public class MessageController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(@Payload MessageDTO chatMessage) {
        chatMessage.setTimestamp(new Date());
        return chatMessage;
    }
}