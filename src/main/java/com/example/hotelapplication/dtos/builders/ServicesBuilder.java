package com.example.hotelapplication.dtos.builders;

import com.example.hotelapplication.dtos.ServicesDTO;
import com.example.hotelapplication.entities.Services;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor
public class ServicesBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static ServicesDTO etoservicesDTO(Services service){
        return modelMapper.map(service, ServicesDTO.class);
    }

    public static Services stoEntity(ServicesDTO servicesDTO){
        return modelMapper.map(servicesDTO, Services.class);
    }
}
