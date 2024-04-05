package com.example.hotelapplication.dtos;

import com.example.hotelapplication.enums.RoomType;
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

    private RoomType roomType;

    private int roomCapacity;

    private double roomRate;

    private String roomDescription;

    private int roomCost;

    private List<ReservationDTO> reservationDTOS;

    private List<ServicesDTO> services;

}
