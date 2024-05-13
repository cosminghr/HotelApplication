package com.example.hotelapplication.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailUserDTO implements Serializable {
    private UUID id;
    private String ownerRef;
    private String name;
    private String emailFrom;
    private String emailTo;

    private String title;

}
