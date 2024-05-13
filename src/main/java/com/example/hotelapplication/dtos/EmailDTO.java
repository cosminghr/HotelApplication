package com.example.hotelapplication.dtos;

import com.example.hotelapplication.enums.StatusEmail;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDTO implements Serializable {
    private UUID id;
    private String name;
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String title;

    private String roomNumbers;
    private LocalDate startDate;
    private LocalDate endDate;
    private int reservationCost;
    private UUID reservationId;

    private LocalDateTime emailDate;
    private StatusEmail emailStatus;

}
