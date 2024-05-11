package com.example.hotelapplication.dtos.builders;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.entities.Reservation;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;


@NoArgsConstructor
public class ReservationBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Reservation stoEntity(ReservationDTO reservationDTO) {
        return modelMapper.map(reservationDTO, Reservation.class);
    }

    public static ReservationDTO etoReservationDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationDTO.class);
    }
}
