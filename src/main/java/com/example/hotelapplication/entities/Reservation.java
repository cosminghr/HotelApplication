
package com.example.hotelapplication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")

    private UUID reservationId;

    @ManyToOne()
    @JoinColumn(name = "personId", referencedColumnName = "id")
    private Person person;

    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "reservations_rooms",
            joinColumns =  @JoinColumn(name = "reservationId"),
            inverseJoinColumns = @JoinColumn(name = "roomId"))
    private List<Rooms> rooms;

    @Column(name = "reservationStart", nullable = false)
    private LocalDate reservationStart;
    @Column(name = "reservationEnd", nullable = false)
    private LocalDate reservationEnd;
    @Column(name = "reservationCost", nullable = false)
    private int reservationCost;

}

