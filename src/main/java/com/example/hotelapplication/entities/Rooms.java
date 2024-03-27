package com.example.hotelapplication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "rooms")
public class Rooms {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID roomId;
    @Column(name = "roomNumber", nullable = false, unique = true)
    private int roomNumber;
    @Column(name = "roomType", nullable = false)
    private String roomType;
    @Column(name = "roomCapacity", nullable = false)
    private int roomCapacity;
    @Column(name = "roomStatus", nullable = false)
    private String roomStatus;
    @Column(name = "roomRate", nullable = false)
    private double roomRate;
    @Column(name = "roomDescription", nullable = false)
    private String roomDescription;
    @Column(name = "roomCost", nullable = false)
    private int roomCost;
    @ManyToOne
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

}