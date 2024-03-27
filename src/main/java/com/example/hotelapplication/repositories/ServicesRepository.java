package com.example.hotelapplication.repositories;

import com.example.hotelapplication.entities.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicesRepository extends JpaRepository<Services, UUID> {
}
