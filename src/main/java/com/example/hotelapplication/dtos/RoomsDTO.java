package com.example.hotelapplication.dtos;

import com.example.hotelapplication.entities.RoomType;
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

    private RoomType roomType;

    private int roomCapacity;


    private double roomRate;

    private String roomDescription;

    private int roomCost;

    private List<ReservationDTO> reservationDTOS;

}
