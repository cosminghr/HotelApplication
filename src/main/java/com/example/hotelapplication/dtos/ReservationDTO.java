package com.example.hotelapplication.dtos;

import com.example.hotelapplication.entities.Rooms;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationDTO {
    private UUID reservationId;
    //private PersonDTO personDTO;
    private PersonDTO person;
    private List<RoomsDTO> rooms;
    private LocalDate reservationStart;
    private LocalDate reservationEnd;
    private int reservationCost;

}

