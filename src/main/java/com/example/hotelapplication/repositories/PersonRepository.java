package com.example.hotelapplication.repositories;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByEmail(String email);
}
