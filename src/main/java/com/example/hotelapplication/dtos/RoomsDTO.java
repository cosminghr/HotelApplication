package com.example.hotelapplication.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomsDTO {

    private UUID roomId;

    private int roomNumber;

    private String roomType;

    private int roomCapacity;

    private String roomStatus;

    private double roomRate;

    private String roomDescription;

    private int roomCost;

    private List<ReservationDTO> reservationDTOS;

}
