package com.example.hotelapplication.entities;

import com.example.hotelapplication.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
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
    private RoomType roomType;
    @Column(name = "roomCapacity", nullable = false)
    private int roomCapacity;
    @Column(name = "roomRate", nullable = false)
    private double roomRate;

    @Column(name = "roomDescription", nullable = false)
    private String roomDescription;
    @Column(name = "roomCost", nullable = false)
    private int roomCost;
    @Column(name="roomImage", nullable = false)
    private String roomImagePath;
    @ManyToMany(mappedBy = "rooms", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Reservation> reservations;

    @ManyToMany(cascade = CascadeType.DETACH, fetch =  FetchType.EAGER)
    @JoinTable(name = "rooms_services",
               joinColumns = @JoinColumn(name = "roomId"),
               inverseJoinColumns = @JoinColumn(name = "serviceId"))
    private List<Services> services;


}