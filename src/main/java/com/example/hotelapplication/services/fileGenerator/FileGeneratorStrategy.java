package com.example.hotelapplication.services.fileGenerator;

import com.example.hotelapplication.dtos.ReservationDTO;

import java.io.IOException;
import java.util.List;

public interface FileGeneratorStrategy {
    default void generateAdmin(List<ReservationDTO> reservationDTOs) throws IOException {

    }

    default void generateClient(ReservationDTO reservationDTO) throws IOException {

    }
}
