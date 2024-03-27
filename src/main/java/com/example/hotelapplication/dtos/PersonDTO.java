package com.example.hotelapplication.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonDTO {
    private UUID id;
    private String name;

    private String email;
    private String password;

    private String address;

    private Date date;

    private String role;
}
