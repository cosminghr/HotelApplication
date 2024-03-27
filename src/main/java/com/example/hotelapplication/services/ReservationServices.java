package com.example.hotelapplication.services;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.builders.ReservationBuilder;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

        // Iterate over each room in the reservation
        for (RoomsDTO roomDTO : roomsDTOs) {
            // Check if room with provided ID exists
            Optional<Rooms> roomOptional = roomsRepository.findById(roomDTO.getRoomId());
            if (roomOptional.isEmpty()) {
                LOGGER.warn("Room with id {} not found. Reservation creation aborted.", roomDTO.getRoomId());
                return null;
            }

            Rooms room = roomOptional.get();

            // Check if room details match
            if (!roomMatches(room, roomDTO)) {
                LOGGER.warn("Room details provided do not match the details of the room with id {}. Reservation creation aborted.", room.getRoomId());
                return null;
            }

            if (isRealTime(reservationDTO)) {
                if (room.getRoomStatus().contains("free")) {
                    // Calculate total cost for the current room
                    int roomCost = calculateTotalCost(reservationDTO);
                    reservationDTO.setReservationCost(roomCost);

                    // Create and save reservation for the current room
                    Reservation reservation = ReservationBuilder.stoEntity(reservationDTO);
                    reservation = reservationRepository.save(reservation);

                    // Update room status and associate reservation with room
                    room.setReservation(reservation);
                    room.setRoomStatus("booked");
                    roomsRepository.save(room);

                    LOGGER.info("Reservation with id {} was inserted for room with id {} in db", reservation.getReservationId(), room.getRoomId());
                } else {
                    LOGGER.warn("The reservation cannot be made for room with id {} because it is already booked!", room.getRoomId());
                    return null;
                }
            } else {
                LOGGER.warn("Reservation start date is in the past or end date is before start date. Reservation creation aborted.");
                return null;
            }
        }

        // Return the reservation id of the first room (assuming all rooms have the same reservation id)
        return reservationDTO.getRooms().get(0).getRoomId();
    }


    /**
     * Updates an existing reservation in the database based on the provided ReservationDTO.
     *
     * @param reservationDTO The updated ReservationDTO object.
     * @return The updated ReservationDTO object, or null if update fails.
     */
    public ReservationDTO updateReservations(ReservationDTO reservationDTO) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationDTO.getReservationId());
        if (optionalReservation.isEmpty()) {
            LOGGER.warn("Reservation with id {} not found. Update operation aborted.", reservationDTO.getReservationId());
            return null;
        }

        Reservation existingReservation = optionalReservation.get();

        // Check if the reservation is in real-time
        if (!isRealTime(reservationDTO)) {
            LOGGER.warn("Reservation start date is in the past or end date is before start date. Update operation aborted.");
            return null;
        }

        // Update reservation fields
        existingReservation.setReservationStart(reservationDTO.getReservationStart());
        existingReservation.setReservationEnd(reservationDTO.getReservationEnd());
        existingReservation.setReservationCost(calculateTotalCost(reservationDTO));

        // Save updated reservation
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
                room.setReservation(null);
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
        LocalDate startDate = reservationDTO.getReservationStart();
        LocalDate endDate = reservationDTO.getReservationEnd();
        long differenceInDays = ChronoUnit.DAYS.between(startDate, endDate);
        //long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);
        int numberOfNights = (int) differenceInDays;
        int roomCost = reservationDTO.getRooms().get(0).getRoomCost();
        return numberOfNights * roomCost;
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
}