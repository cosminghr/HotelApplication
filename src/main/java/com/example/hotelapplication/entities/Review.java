package com.example.hotelapplication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")

    private UUID reviewId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private String comment;

    @ManyToOne()
    @JoinColumn(name = "personId", referencedColumnName = "id")
    private Person person;

    @ManyToOne()
    @JoinColumn(name = "roomId", referencedColumnName = "roomId")
    private Rooms room;

}
