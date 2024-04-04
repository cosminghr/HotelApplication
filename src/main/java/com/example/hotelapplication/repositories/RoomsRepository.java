package com.example.hotelapplication.repositories;

import com.example.hotelapplication.entities.Rooms;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomsRepository extends JpaRepository<Rooms, UUID> {

    @EntityGraph(attributePaths = "reservations")
    Optional<Rooms> findById(UUID id);
}
