
package com.example.hotelapplication.repositories;

import com.example.hotelapplication.entities.Reservation;
import com.example.hotelapplication.entities.Rooms;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByPersonId(UUID personId);

}
