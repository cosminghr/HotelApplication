package com.example.hotelapplication.services;

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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream()
                .map(ReservationBuilder::etoReservationDTO)
                .collect(Collectors.toList());
    }

    public List<RoomsDTO> findAllRooms() {
        List<Rooms> rooms = roomsRepository.findAll();
        return rooms.stream()
                .map(RoomsBuilder::etoRoomsDTO)
                .collect(Collectors.toList());
    }


    public List<PersonDTO> findAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(PersonBuilder::etoPersonDTO)
                .collect(Collectors.toList());
    }

    public RoomsDTO findRoomByIdInReservation(UUID id) {
        Optional<Rooms> optionalRooms = roomsRepository.findById(id);
        if (optionalRooms.isPresent()) {
            LOGGER.info("Room with id {} was found in db", id);
            return RoomsBuilder.etoRoomsDTO(optionalRooms.get());
        } else {
            LOGGER.error("Room with id {} was not found in db", id);
            return null;
        }
    }

    public PersonDTO findPersonByIdInReservation(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            return null;
        }
        LOGGER.info("Person with id {} was found in db", id);
        return PersonBuilder.etoPersonDTO(personOptional.get());
    }
    /**
     * Retrieves a specific reservation by ID.
     *
     * @param id The UUID of the reservation to retrieve.
     * @return The ReservationDTO object if found, otherwise null.
     */
    public ReservationDTO findReservationsById(UUID id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (!optionalReservation.isPresent()) {
            LOGGER.error("Reservation with id {} was not found in db", id);
            return null;
        }
        LOGGER.info("Reservation with id {} was found in db", id);
        return ReservationBuilder.etoReservationDTO(optionalReservation.get());
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @return The generated UUID for the newly inserted reservation, or null if insertion fails.
     */
    public UUID insertReservations(ReservationDTO reservationDTO) {
        Optional<Person> personOptional = personRepository.findById(reservationDTO.getPerson().getId());
        if (personOptional.isEmpty()) {
            LOGGER.warn("Person with id {} not found. Reservation creation aborted.", reservationDTO.getPerson().getId());
            return null;
        }

        List<RoomsDTO> roomsDTOs = reservationDTO.getRooms();
        if (roomsDTOs.isEmpty()) {
            LOGGER.warn("List of rooms is empty. Reservation creation aborted.");
            return null;
        }

        List<RoomsDTO> roomsDTOsForRes = new ArrayList<>();
        for (RoomsDTO roomDTO : roomsDTOs) {
            Optional<Rooms> roomOptional = roomsRepository.findById(roomDTO.getRoomId());
            if (roomOptional.isEmpty()) {
                LOGGER.warn("Room with id {} not found. Reservation creation aborted.", roomDTO.getRoomId());
                return null;
            }
            if (roomDTO.getReservationDTOS() == null) {
                roomDTO.setReservationDTOS(new ArrayList<>());
            }
            if (!isRoomAvailableForPeriodWithoutCurrentReservation(roomDTO.getRoomId(), reservationDTO.getReservationStart(), reservationDTO.getReservationEnd(), reservationDTO.getReservationId())) {
                LOGGER.warn("Room with id {} is not available for the specified period. Reservation creation aborted.", roomDTO.getRoomId());
                return null;
            }else{
                roomsDTOsForRes.add(roomDTO);
                roomsDTOsForRes.get(0).getReservationDTOS().add(reservationDTO);
            }
        }

        if (!isRealTime(reservationDTO)) {
            LOGGER.warn("Reservation start date is in the past or end date is before start date. Reservation creation aborted.");
            return null;
        }

        reservationDTO.setReservationCost(calculateTotalCost(reservationDTO));
        reservationDTO.setRooms(roomsDTOsForRes);

        Reservation reservation = ReservationBuilder.stoEntity(reservationDTO);
        reservation = reservationRepository.save(reservation);

        LOGGER.info("Reservation with id {} was inserted in db", reservation.getReservationId());
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

        Reservation existingReservation = optionalReservation.get();

        // Check if the rooms are available for the updated period
        List<UUID> roomIds = reservationDTO.getRooms().stream()
                .map(RoomsDTO::getRoomId)
                .toList();
        LocalDate newStart = reservationDTO.getReservationStart();
        LocalDate newEnd = reservationDTO.getReservationEnd();
        for (UUID roomId : roomIds) {
            if (!isRoomAvailableForPeriodWithoutCurrentReservation(roomId, newStart, newEnd, existingReservation.getReservationId())) {
                LOGGER.warn("Room with id {} is not available for the updated period. Update operation aborted.", roomId);
                return null;
            }
        }

        // Update reservation fields
        existingReservation.setReservationStart(reservationDTO.getReservationStart());
        existingReservation.setReservationEnd(reservationDTO.getReservationEnd());
        existingReservation.setReservationCost(calculateTotalCost(reservationDTO));

        // Save the updated reservation in the database
        Reservation updatedReservation = reservationRepository.save(existingReservation);

        LOGGER.info("Reservation with id {} was updated in db", existingReservation.getReservationId());
        return ReservationBuilder.etoReservationDTO(updatedReservation);
    }

    /**
     * Deletes a reservation from the database based on the provided UUID.
     *
     * @param id The UUID of the reservation to be deleted.
     */
    public void deleteReservations(UUID id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            List<Rooms> rooms = reservation.getRooms();
            for (Rooms room : rooms) {
                room.getReservations().remove(reservation);
                room.setRoomStatus("free");
                roomsRepository.save(room);
            }
            reservationRepository.deleteById(id);
            LOGGER.info("Reservation with id {} deleted successfully.", id);
        } else {
            LOGGER.info("Reservation with id {} not found. Delete operation aborted.", id);
        }
    }

    /**
     * Calculates the total cost of the reservation.
     *
     * @param reservationDTO The ReservationDTO object containing reservation details.
     * @return The total cost of the reservation.
     */
    public int calculateTotalCost(ReservationDTO reservationDTO) {
        List<RoomsDTO> roomsDTOS = reservationDTO.getRooms();
        int totalCost = 0; // Initialize total cost

        // Iterate through each room in the reservation
        for(RoomsDTO roomDTO: roomsDTOS) {
            LocalDate startDate = reservationDTO.getReservationStart();
            LocalDate endDate = reservationDTO.getReservationEnd();
            long differenceInDays = ChronoUnit.DAYS.between(startDate, endDate);
            int numberOfNights = (int) differenceInDays;

            // Get the cost of the current room and calculate the cost for the stay
            int roomCost = roomDTO.getRoomCost();
            int roomTotalCost = numberOfNights * roomCost;

            // Add the cost of the current room to the total cost
            totalCost += roomTotalCost;
        }

        return totalCost;
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
     * Checks if the details of the provided room match the details of the actual room.
     *
     * @param room   The actual room entity.
     * @param roomDTO The RoomDTO object containing room details.
     * @return True if the details match, false otherwise.
     */
    private boolean roomMatches(Rooms room, RoomsDTO roomDTO) {
        return room.getRoomId().equals(roomDTO.getRoomId()) &&
                room.getRoomStatus().equals(roomDTO.getRoomStatus()) &&
                room.getRoomCapacity() == roomDTO.getRoomCapacity() &&
                room.getRoomNumber() == roomDTO.getRoomNumber() &&
                room.getRoomDescription().equals(roomDTO.getRoomDescription()) &&
                room.getRoomCost() == roomDTO.getRoomCost() &&
                room.getRoomRate() == roomDTO.getRoomRate() &&
                room.getRoomType().equals(roomDTO.getRoomType());
    }

    public boolean isRoomAvailableForPeriodWithoutCurrentReservation(UUID roomId, LocalDate newReservationStart, LocalDate newReservationEnd, UUID currentReservationId) {
        // Retrieve the room by its ID
        Optional<Rooms> roomOptional = roomsRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            LOGGER.warn("Room with id {} not found.", roomId);
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


}