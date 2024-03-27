package com.example.hotelapplication.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDTO {

    private UUID serviceId;
    private String serviceName;
    private int roomId;
    private String serviceType;
    private String serviceStatus;
    private String serviceDescription;
    private double serviceCost;
}
