package com.example.hotelapplication.services;
import com.example.hotelapplication.constants.*;
import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.builders.PersonBuilder;
import com.example.hotelapplication.dtos.builders.ReservationBuilder;
import com.example.hotelapplication.dtos.builders.RoomsBuilder;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.entities.Reservation;
import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.repositories.ReservationRepository;
import com.example.hotelapplication.repositories.RoomsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.hotelapplication.constants.DiscountPolicy.*;
import static com.example.hotelapplication.constants.ReservationsConstants.*;

/**
 * Service class for managing operations related to Reservation entities.
 */
@Service
public class ReservationServices {
    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final RoomsRepository roomsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServices.class);

    /**
     * Constructor for ReservationServices.
     *
     * @param reservationRepository The repository for Reservation entities.
     * @param personRepository      The repository for Person entities.
     * @param roomsRepository       The repository for Rooms entities.
     */
    public ReservationServices(ReservationRepository reservationRepository, PersonRepository personRepository, RoomsRepository roomsRepository) {
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
        this.roomsRepository = roomsRepository;
    }

    /**
     * Retrieves a list of ReservationDTO objects representing all reservations in the database.
     *
     * @return List of ReservationDTO objects.
     */
    public List<ReservationDTO> findReservations() {
        // Retrieve all reservations from the database and map them to ReservationDTO objects
        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream()
                .map(ReservationBuilder::etoReservationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of RoomsDTO objects representing all rooms in the database.
     *
     * @return List of RoomsDTO objects.
     */
    public List<RoomsDTO> findAllRooms() {
        // Retrieve all rooms from the database and map them to RoomsDTO objects
        List<Rooms> rooms = roomsRepository.findAll();
        return rooms.stream()
                .map(RoomsBuilder::etoRoomsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of PersonDTO objects representing all persons in the database.
     *
     * @return List of PersonDTO objects.
     */
    public List<PersonDTO> findAllPersons() {
        // Retrieve all persons from the database and map them to PersonDTO objects
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(PersonBuilder::etoPersonDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a room by its ID.
     *
     * @param id The ID of the room to retrieve.
     * @return The RoomsDTO object if found, otherwise null.
     */
    public RoomsDTO findRoomByIdInReservation(UUID id) {
        // Retrieve the room from the repository by ID
        Optional<Rooms> optionalRooms = roomsRepository.findById(id);
        if (optionalRooms.isPresent()) {
            LOGGER.info(ROOM_FOUND, id);
            return RoomsBuilder.etoRoomsDTO(optionalRooms.get());
        } else {
            LOGGER.error(ROOM_NOT_FOUND, id);
            return null;
        }
    }

    /**
     * Finds a person by its ID.
     *
     * @param id The ID of the person to retrieve.
     * @return The PersonDTO object if found, otherwise null.
     */
    public PersonDTO findPersonByIdInReservation(UUID id) {
        // Retrieve the person from the repository by ID
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error(PERSON_FOUND, id);
            return null;
        }
        LOGGER.info(PERSON_NOT_FOUND, id);
        return PersonBuilder.etoPersonDTO(personOptional.get());
    }

    /**
     * Retrieves a specific reservation by its ID.
     *
     * @param id The UUID of the reservation to retrieve.
     * @return The ReservationDTO object if found, otherwise null.
     */
    public ReservationDTO findReservationsById(UUID id) {
        // Retrieve the reservation from the repository by ID
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (!optionalReservation.isPresent()) {
            LOGGER.error(RESERVATION_NOT_FOUND, id);
            return null;
        }
        LOGGER.info(RESERVATION_FOUND, id);
        return ReservationBuilder.etoReservationDTO(optionalReservation.get());
    }

    /**
     * Retrieves a list of reservations associated with a specific person.
     *
     * @param personId The UUID of the person.
     * @return List of ReservationDTO objects associated with the person.
     */
    public List<ReservationDTO> findReservationsByPerson(UUID personId){
        // Retrieve the person from the repository by ID
        Optional<Person> person = personRepository.findById(personId);
        if (person.isPresent()) {
            // Retrieve all reservations from the repository
            List<Reservation> reservationList = reservationRepository.findAll();
            List<ReservationDTO> personReservations = new ArrayList<>();

            // Filter reservations by person
            for(Reservation reservation: reservationList){
                if(reservation.getPerson().equals(person.get())){
                    personReservations.add(ReservationBuilder.etoReservationDTO(reservation));
                }
            }
            return personReservations;
        }
        return Collections.emptyList(); // Return empty list if person not found
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @return The generated UUID for the newly inserted reservation, or null if insertion fails.
     */
    public UUID insertReservations(ReservationDTO reservationDTO) {
        // Retrieve the person from the repository by ID
        Optional<Person> personOptional = personRepository.findById(reservationDTO.getPerson().getId());
        if (personOptional.isEmpty()) {
            LOGGER.warn(PERSON_FOUND, reservationDTO.getPerson().getId());
            return null;
        }

        // Check if the list of rooms is empty
        List<RoomsDTO> roomsDTOs = reservationDTO.getRooms();
        if (roomsDTOs.isEmpty()) {
            LOGGER.warn(LIST_EMPTY);
            return null;
        }

        // Validate each room and its availability for the reservation period
        List<RoomsDTO> roomsDTOsForRes = new ArrayList<>();
        for (RoomsDTO roomDTO : roomsDTOs) {
            Optional<Rooms> roomOptional = roomsRepository.findById(roomDTO.getRoomId());
            if (roomOptional.isEmpty()) {
                LOGGER.warn(ROOM_NOT_FOUND, roomDTO.getRoomId());
                return null;
            }
            if (!isRoomAvailableForPeriodWithoutCurrentReservation(roomDTO.getRoomId(), reservationDTO.getReservationStart(), reservationDTO.getReservationEnd(), reservationDTO.getReservationId())) {
                LOGGER.warn(ROOM_NOT_AVAILABLE, roomDTO.getRoomId());
                return null;
            } else {
                roomsDTOsForRes.add(roomDTO);
            }
        }

        // Check if reservation start date is in real-time
        if (!isRealTime(reservationDTO)) {
            LOGGER.warn(BAD_TIME);
            return null;
        }

        int initialCost = calculateTotalCost(reservationDTO);
        int finalCost = calculateTotalCostWithDiscount(reservationDTO);

        // Update total cost in the reservation
        reservationDTO.setReservationInitialCost(initialCost);
        reservationDTO.setReservationFinalCost(finalCost);
        reservationDTO.setRooms(roomsDTOsForRes);

        // Save the reservation in the database
        Reservation reservation = ReservationBuilder.stoEntity(reservationDTO);
        reservation = reservationRepository.save(reservation);

        LOGGER.info(RESERVATION_INSERT, reservation.getReservationId());
        return reservation.getReservationId();
    }


    /**
     * Updates an existing reservation in the database based on the provided ReservationDTO.
     *
     * @param reservationDTO The updated ReservationDTO object.
     * @return The updated ReservationDTO object, or null if update fails.
     */
    public ReservationDTO updateReservations(ReservationDTO reservationDTO) {
        // Retrieve the existing reservation from the database
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationDTO.getReservationId());
        if (optionalReservation.isEmpty()) {
            LOGGER.warn("Reservation with id {} not found. Update operation aborted.", reservationDTO.getReservationId());
            return null;
        }

        // Retrieve the existing reservation
        Reservation existingReservation = optionalReservation.get();

        // Check if the rooms are available for the updated period
        List<UUID> roomIds = reservationDTO.getRooms().stream()
                .map(RoomsDTO::getRoomId)
                .toList();
        LocalDate newStart = reservationDTO.getReservationStart();
        LocalDate newEnd = reservationDTO.getReservationEnd();
        for (UUID roomId : roomIds) {
            if (!isRoomAvailableForPeriodWithoutCurrentReservation(roomId, newStart, newEnd, existingReservation.getReservationId())) {
                LOGGER.warn(ROOM_NOT_AVAILABLE, roomId);
                return null;
            }
        }

        // Update reservation fields
        existingReservation.setReservationStart(reservationDTO.getReservationStart());
        existingReservation.setReservationEnd(reservationDTO.getReservationEnd());

        // Set the updated list of rooms to the reservation
        List<Rooms> rooms = reservationDTO.getRooms().stream()
                .map(RoomsBuilder::stoEntity)
                .collect(Collectors.toList());
        existingReservation.setRooms(rooms);

        System.out.println(reservationDTO.toString());
        // Calculate the new total cost after applying the discount
        int initialCost = calculateTotalCost(reservationDTO);
        int finalCost = calculateTotalCostWithDiscount(reservationDTO);

        // Update total cost in the reservation
        existingReservation.setReservationInitialCost(initialCost);
        existingReservation.setReservationFinalCost(finalCost);

        // Save the updated reservation in the database
        Reservation updatedReservation = reservationRepository.save(existingReservation);

        LOGGER.info(RESERVATION_UPDATED, existingReservation.getReservationId());
        return ReservationBuilder.etoReservationDTO(updatedReservation);
    }


    /**
     * Deletes a reservation from the database based on the provided UUID.
     *
     * @param id The UUID of the reservation to be deleted.
     */
    public void deleteReservations(UUID id) {
        // Retrieve the reservation from the repository by ID
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            // Retrieve the reservation
            Reservation reservation = optionalReservation.get();

            // Remove the association between rooms and the reservation
            List<Rooms> rooms = reservation.getRooms();
            for (Rooms room : rooms) {
                room.getReservations().remove(reservation);
                roomsRepository.save(room);
            }

            // Delete the reservation
            reservationRepository.deleteById(id);
            LOGGER.info(RESERVATION_DELETED, id);
        } else {
            LOGGER.info(RESERVATION_NOT_DELETED, id);
        }
    }

    /**
     * Calculates the total cost of the reservation.
     *
     * @param reservationDTO The ReservationDTO object containing reservation details.
     * @return The total cost of the reservation.
     */
    public int calculateTotalCost(ReservationDTO reservationDTO) {
        // Initialize total cost
        int totalCost = 0;

        // Iterate through each room in the reservation
        for(RoomsDTO roomDTO: reservationDTO.getRooms()) {
            // Calculate the number of nights for the reservation
            long differenceInDays = ChronoUnit.DAYS.between(reservationDTO.getReservationStart(), reservationDTO.getReservationEnd());
            int numberOfNights = (int) differenceInDays;

            // Get the cost per night for the current room
            int roomCost = roomDTO.getRoomCost();

            // Calculate the total cost for the current room and add it to the total cost
            totalCost += numberOfNights * roomCost;
        }

        // Log the total price
        LOGGER.info("Total Price: {}", totalCost);

        return totalCost; // Return the total cost without discount
    }

    public int calculateTotalCostWithDiscount(ReservationDTO reservationDTO) {
        // Get the person's ID from the reservation
        UUID personId = reservationDTO.getPerson().getId();

        // Calculate the discount for the person
        int discount = calculatePersonDiscount(personId, reservationDTO.getReservationId());

        // Calculate the total cost without discount
        int totalCostWithoutDiscount = calculateTotalCost(reservationDTO);

        // Calculate the new total cost after applying the discount
        int discountedCost = totalCostWithoutDiscount - (totalCostWithoutDiscount * discount / 100);

        // Log the old and new total prices
        LOGGER.info(OLD_COST, totalCostWithoutDiscount);
        LOGGER.info(NEW_COST, discount, discountedCost);

        return discountedCost; // Return the new total cost after discount
    }



    /**
     * Checks if the reservation is in real-time (not in the past and end date is after start date).
     *
     * @param reservationDTO The ReservationDTO object containing reservation details.
     * @return True if the reservation is in real-time, false otherwise.
     */
    public boolean isRealTime(ReservationDTO reservationDTO) {
        LocalDate localDate = LocalDate.now();
        LocalDate reservationStartDate = reservationDTO.getReservationStart();
        LocalDate reservationEndDate = reservationDTO.getReservationEnd();
        return !reservationStartDate.isBefore(localDate) && !reservationEndDate.isBefore(reservationStartDate);
    }

    /**
     * Checks if a room is available for the specified period excluding the current reservation.
     *
     * @param roomId              The ID of the room.
     * @param newReservationStart The start date of the new reservation.
     * @param newReservationEnd   The end date of the new reservation.
     * @param currentReservationId The ID of the current reservation being updated.
     * @return True if the room is available, false otherwise.
     */
    public boolean isRoomAvailableForPeriodWithoutCurrentReservation(UUID roomId, LocalDate newReservationStart, LocalDate newReservationEnd, UUID currentReservationId) {
        // Retrieve the room by its ID
        Optional<Rooms> roomOptional = roomsRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            LOGGER.warn(ROOM_NOT_FOUND, roomId);
            return false; // Room not found, consider it unavailable
        }
        Rooms room = roomOptional.get();

        // Retrieve all reservations for the room
        List<Reservation> reservations = room.getReservations();

        // Check if there are any reservations for the room
        if (reservations == null || reservations.isEmpty()) {
            return true; // No reservations, room is available
        }

        // Check if the room is available for the given period excluding the current reservation being updated
        for (Reservation reservation : reservations) {
            if (!reservation.getReservationId().equals(currentReservationId)) {
                LocalDate reservationStart = reservation.getReservationStart();
                LocalDate reservationEnd = reservation.getReservationEnd();

                // Check for overlap between existing reservation and new reservation
                if (!(newReservationEnd.isBefore(reservationStart) || newReservationStart.isAfter(reservationEnd))) {
                    return false; // Room is not available for the given period
                }
            }
        }

        return true; // Room is available for the given period excluding the current reservation
    }

    /**
     * Calculates the number of reservations for each person and applies a discount if applicable.
     *
     * @param personId        The UUID of the person.
     * @return The discount percentage for the person.
     */
    public int calculatePersonDiscount(UUID personId, UUID currentReservationId) {
        // Retrieve the person from the repository by ID
        Optional<Person> personOptional = personRepository.findById(personId);
        if (personOptional.isEmpty()) {
            LOGGER.warn(PERSON_NOT_FOUND, personId);
            return 0; // Person not found, no discount
        }
        Person person = personOptional.get();

        // Retrieve all reservations associated with the person excluding the current reservation
        List<Reservation> personReservations = reservationRepository.findByPersonId(personId);
        personReservations.removeIf(reservation -> reservation.getReservationId().equals(currentReservationId));

        // Calculate the number of reservations for the person
        int numberOfReservations = personReservations.size();

        // Apply discount based on the discount policy
        if(numberOfReservations >= REQUIRED_NUMBER_OF_RESERVATIONS_SMALL){
            if (numberOfReservations >= DiscountPolicy.REQUIRED_NUMBER_OF_RESERVATIONS_GREAT) {
                return DiscountPolicy.DISCOUNT_PERCENTAGE_GREAT;
            } else if (numberOfReservations >= DiscountPolicy.REQUIRED_NUMBER_OF_RESERVATIONS_MEDIUM) {
                return DiscountPolicy.DISCOUNT_PERCENTAGE_MEDIUM;
            } else if (numberOfReservations >= DiscountPolicy.REQUIRED_NUMBER_OF_RESERVATIONS_SMALL) {
                return DiscountPolicy.DISCOUNT_PERCENTAGE_SMALL;
            }
        }

        return 0; // No discount
    }



}
