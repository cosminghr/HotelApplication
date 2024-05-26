package com.example.hotelapplication.generator;


import com.example.hotelapplication.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAll();
}