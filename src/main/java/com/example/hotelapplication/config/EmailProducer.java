package com.example.hotelapplication.config;

import com.example.hotelapplication.dtos.EmailDTO;
import com.example.hotelapplication.entities.Reservation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
@Service
@Configuration
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(EmailDTO emailDTO){
        rabbitTemplate.convertAndSend("email-exchange", "email-routing-key", emailDTO);
    }
}
