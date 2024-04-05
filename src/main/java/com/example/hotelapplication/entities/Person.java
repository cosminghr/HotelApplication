package com.example.hotelapplication.entities;

import com.example.hotelapplication.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.GenericGenerator;


import java.util.Date;
import java.util.List;
import java.util.UUID;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")

    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "role", nullable = false)
    private RoleType role;

   @OneToMany(mappedBy = "person", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
   private List<Reservation> reservations;
}
