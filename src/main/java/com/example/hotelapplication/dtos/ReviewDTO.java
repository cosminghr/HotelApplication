package com.example.hotelapplication.dtos;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewDTO {
    private UUID id;

    private int rating;
    private String comment;
    private PersonDTO person;
    private RoomsDTO room;

}
