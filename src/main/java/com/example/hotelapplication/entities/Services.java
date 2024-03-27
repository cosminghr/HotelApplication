package com.example.hotelapplication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Services {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID serviceId;
    @Column(name = "serviceName", nullable = false)
    private String serviceName;
    @Column(name = "roomId", nullable = false)
    private int roomId;
    @Column(name = "serviceType", nullable = false)
    private String serviceType;
    @Column(name = "serviceStatus", nullable = false)
    private String serviceStatus;
    @Column(name = "serviceDescription", nullable = false)
    private String serviceDescription;
    @Column(name = "serviceCost", nullable = false)
    private double serviceCost;


}
