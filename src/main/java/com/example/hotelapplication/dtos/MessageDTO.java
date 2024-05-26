package com.example.hotelapplication.dtos;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Getter
@Setter
public class MessageDTO {

    private String nickname;
    private String content;
    private Date timestamp;
}