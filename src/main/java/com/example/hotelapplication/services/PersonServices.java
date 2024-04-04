package com.example.hotelapplication.services;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.builders.PersonBuilder;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.entities.Reservation;
import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.repositories.ReservationRepository;
import com.example.hotelapplication.repositories.RoomsRepository;
import jakarta.transaction.TransactionScoped;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing operations related to Person entities.
 */
@Service
public class PersonServices {
    private final PersonRepository personRepository;
    private final ReservationRepository reservationRepository;
    private final RoomsRepository roomsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServices.class);

    /**
     * Constructor for PersonServices.
     *
     * @param personRepository      The repository for Person entities.
     * @param reservationRepository The repository for Reservation entities.
     * @param roomsRepository       The repository for Rooms entities.
     */
    public PersonServices(PersonRepository personRepository, ReservationRepository reservationRepository, RoomsRepository roomsRepository) {
        this.personRepository = personRepository;
        this.reservationRepository = reservationRepository;
        this.roomsRepository = roomsRepository;
    }

    /**
     * Retrieves a list of PersonDTO objects representing all persons in the database.
     *
     * @return List of PersonDTO objects.
     */
    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::etoPersonDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a person by ID.
     *
     * @param id The ID of the person.
     * @return The PersonDTO object if found, else null.
     */
    public PersonDTO findPersonById(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            return null;
        }
        LOGGER.info("Person with id {} was found in db", id);
        return PersonBuilder.etoPersonDTO(personOptional.get());
    }

    /**
     * Inserts a new person into the database.
     *
     * @param personDTO The PersonDTO object to be inserted.
     * @return The generated UUID for the newly inserted person if insertion is successful, else null.
     */
    public UUID insertPerson(PersonDTO personDTO) {
        Optional<Person> existingPerson = personRepository.findAll().stream()
                .filter(person -> person.getEmail().equals(personDTO.getEmail()))
                .findFirst();

        if (existingPerson.isPresent()) {
            LOGGER.warn("Email address {} is already in use by another person. Insertion aborted.", personDTO.getEmail());
            return null;
        }

        Person person = PersonBuilder.stoEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.info("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    /**
     * Updates an existing person in the database based on the provided PersonDTO.
     *
     * @param personDTO The updated PersonDTO object.
     * @return The updated PersonDTO object if the person is found and updated successfully, else the original PersonDTO object.
     */
    public PersonDTO updatePerson(PersonDTO personDTO) {
        Optional<Person> optionalPerson = personRepository.findById(personDTO.getId());
        if (optionalPerson.isPresent()) {
            Person existingPerson = optionalPerson.get();
            // Update fields based on the values from updatedPersonDTO
            existingPerson.setName(personDTO.getName());
            existingPerson.setEmail(personDTO.getEmail());
            existingPerson.setPassword(personDTO.getPassword());
            existingPerson.setAddress(personDTO.getAddress());
            existingPerson.setDate(personDTO.getDate());

            Person updatedPerson = personRepository.save(existingPerson);
            LOGGER.info("Person with id {} was updated in db", existingPerson.getId());
            return PersonBuilder.etoPersonDTO(updatedPerson);
        } else {
            LOGGER.warn("Person with id {} not found. Update operation aborted.", personDTO.getId());
            return personDTO;
        }
    }

    /**
     * Deletes a person from the database based on the provided UUID.
     *
     * @param id The UUID of the person to be deleted.
     */
    @TransactionScoped
    public void deletePerson(UUID id) {
        Optional<Person> optionalPerson = personRepository.findById(id);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            List<Reservation> reservations = person.getReservations();

            for (Reservation reservation : reservations) {
                List<Rooms> rooms = reservation.getRooms();
                for (Rooms room : rooms) {
                    // Remove the association with the reservation
                    room.setReservations(null);
                    roomsRepository.save(room);
                }
                // Delete the reservation
                reservationRepository.delete(reservation);
            }

            // Now delete the person
            personRepository.deleteById(id);

            LOGGER.info("Person with id {} and associated reservations deleted successfully.", id);
        } else {
            LOGGER.info("Person with id {} not found. Delete operation aborted.", id);
        }
    }
}