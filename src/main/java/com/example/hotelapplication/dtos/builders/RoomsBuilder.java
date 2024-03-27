package com.example.hotelapplication.dtos.builders;

import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.entities.Rooms;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor
public class RoomsBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();
    public static RoomsDTO etoRoomsDTO(Rooms rooms){
        return modelMapper.map(rooms, RoomsDTO.class);
    }

    public static Rooms stoEntity(RoomsDTO roomsDTO){
        return modelMapper.map(roomsDTO, Rooms.class);
    }
}
