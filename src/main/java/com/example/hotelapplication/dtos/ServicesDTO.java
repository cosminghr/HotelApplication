package com.example.hotelapplication.dtos;

import com.example.hotelapplication.enums.ServiceStatus;
import com.example.hotelapplication.enums.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDTO {

    private UUID serviceId;
    private String serviceName;
    private String serviceDescription;
    private List<RoomsDTO> rooms;
}
