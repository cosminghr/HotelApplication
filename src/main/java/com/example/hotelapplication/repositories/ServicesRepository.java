package com.example.hotelapplication.repositories;

import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.entities.Services;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServicesRepository extends JpaRepository<Services, UUID> {

}
