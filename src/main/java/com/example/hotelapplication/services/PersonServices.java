package com.example.hotelapplication.services;

import com.example.hotelapplication.dtos.EmailDTO;
import com.example.hotelapplication.dtos.EmailUserDTO;
import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.builders.PersonBuilder;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.entities.Reservation;
import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.exceptions.EmailSendingException;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.repositories.ReservationRepository;
import com.example.hotelapplication.repositories.RoomsRepository;
import jakarta.transaction.TransactionScoped;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.hotelapplication.constants.PersonConstants.*;

/**
 * Service class for managing operations related to Person entities.
 */
@Service
public class PersonServices {
    private final PersonRepository personRepository;
    private final ReservationRepository reservationRepository;
    private final RoomsRepository roomsRepository;
    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServices.class);

    /**
     * Constructor for PersonServices.
     *
     * @param personRepository      The repository for Person entities.
     * @param reservationRepository The repository for Reservation entities.
     * @param roomsRepository       The repository for Rooms entities.
     * @param restTemplate
     */
    public PersonServices(PersonRepository personRepository, ReservationRepository reservationRepository, RoomsRepository roomsRepository, RestTemplate restTemplate) {
        this.personRepository = personRepository;
        this.reservationRepository = reservationRepository;
        this.roomsRepository = roomsRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves a list of PersonDTO objects representing all persons in the database.
     *
     * @return List of PersonDTO objects.
     */
    public List<PersonDTO> findPersons() {
        // Retrieve all persons from the database and map them to PersonDTO objects
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
        // Retrieve the person from the repository by ID
        Optional<Person> personOptional = personRepository.findById(id);
        // Check if the person exists, if not, log an error
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            return null;
        }
        LOGGER.info("Person with id {} was found in db", id);
        // Convert the entity to DTO and return
        return PersonBuilder.etoPersonDTO(personOptional.get());
    }

    /**
     * Finds a person by email.
     *
     * @param email The email address of the person.
     * @return The PersonDTO object if found, else null.
     */
    public PersonDTO findPersonByEmail(String email) {
        // Retrieve the person from the repository by email
        Optional<Person> personOptional = personRepository.findByEmail(email);
        // Check if the person exists, if not, log an error
        if (!personOptional.isPresent()) {
            LOGGER.error(PERSON_NOT_FOUND, email);
            return null;
        }
        LOGGER.info(PERSON_FOUND, email);
        // Convert the entity to DTO and return
        return PersonBuilder.etoPersonDTO(personOptional.get());
    }

    /**
     * Authenticates a person based on email and password.
     *
     * @param email    The email address of the person.
     * @param password The password of the person.
     * @return The authenticated PersonDTO object if successful, else null.
     */
    public PersonDTO authenticate(String email, String password) {
        // Find the person by email
        PersonDTO personDTO = findPersonByEmail(email);
        // Check if the person exists and the provided password matches
        if (personDTO != null && personDTO.getPassword().equals(password)) {
            LOGGER.info(PERSON_IN, email);
            return personDTO;
        }
        LOGGER.info(PERSON_NOT_IN, email);
        return null; // Authentication failed
    }

    /**
     * Inserts a new person into the database.
     *
     * @param personDTO The PersonDTO object to be inserted.
     * @return The generated UUID for the newly inserted person if insertion is successful, else null.
     */
    public UUID insertPerson(PersonDTO personDTO) {
        // Check if a person with the same email already exists
        Optional<Person> existingPerson = personRepository.findAll().stream()
                .filter(person -> person.getEmail().equals(personDTO.getEmail()))
                .findFirst();
        // If a person with the same email exists, log a warning and abort insertion
        if (existingPerson.isPresent()) {
            LOGGER.warn(EMAIL_NOT_FOUND, personDTO.getEmail());
            return null;
        }
        // Convert the DTO to entity and save it to the database
        Person person = PersonBuilder.stoEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.info(PERSON_INSERT, person.getId());
        return person.getId();
    }

    /**
     * Updates an existing person in the database based on the provided PersonDTO.
     *
     * @param personDTO The updated PersonDTO object.
     * @return The updated PersonDTO object if the person is found and updated successfully, else the original PersonDTO object.
     */
    public PersonDTO updatePerson(PersonDTO personDTO) {
        // Find the person by ID
        Optional<Person> optionalPerson = personRepository.findById(personDTO.getId());
        if (optionalPerson.isPresent()) {
            // If the person exists, update its fields with the values from the DTO
            Person existingPerson = optionalPerson.get();
            existingPerson.setName(personDTO.getName());
            existingPerson.setEmail(personDTO.getEmail());
            existingPerson.setPassword(personDTO.getPassword());
            existingPerson.setAddress(personDTO.getAddress());
            existingPerson.setDate(personDTO.getDate());
            // Save the updated person to the database
            Person updatedPerson = personRepository.save(existingPerson);
            LOGGER.info(PERSON_UPDATED, existingPerson.getId());
            // Convert the updated entity to DTO and return
            return PersonBuilder.etoPersonDTO(updatedPerson);
        } else {
            // If the person is not found, log a warning and return the original DTO
            LOGGER.warn(PERSON_NOT_UPDATED, personDTO.getId());
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
        // Find the person by ID
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            // If the person is found, delete its associated reservations
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
            LOGGER.info(PERSON_DELETE, id);
        } else {
            // If the person is not found, log a message
            LOGGER.info(PERSON_NOT_DELETE, id);
        }
    }

    public void sendEmailToUser(PersonDTO personDTO, String emailTitle, String emailDescription) throws EmailSendingException {

        EmailUserDTO emailDTO = new EmailUserDTO();
        emailDTO.setEmailTo(personDTO.getEmail());
        emailDTO.setEmailFrom("ghermancosmin112@gmail.com");
        emailDTO.setName(personDTO.getName());
        emailDTO.setTitle(emailTitle);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmailUserDTO> requestEntity = new HttpEntity<>(emailDTO, headers);
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                "http://localhost:8081/emails/sending-email",
                HttpMethod.POST,
                requestEntity,
                Boolean.class
        );
        System.out.println("dadadadaadad"+responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("Email-ul a fost trimis cu succes!");
        } else {
            throw new EmailSendingException("Eroare la trimiterea email-ului!");
        }
    }
}
