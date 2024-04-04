package com.example.hotelapplication.services;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.builders.RoomsBuilder;
import com.example.hotelapplication.entities.Reservation;
import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.repositories.ReservationRepository;
import com.example.hotelapplication.repositories.RoomsRepository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing operations related to Rooms entities.
 */
@Service
public class RoomsServices {
    private final RoomsRepository roomsRepository;
    private final ReservationRepository reservationRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsServices.class);

    /**
     * Constructor for RoomsServices.
     *
     * @param roomsRepository       The repository for Rooms entities.
     * @param reservationRepository The repository for Reservation entities.
     */
    public RoomsServices(RoomsRepository roomsRepository, ReservationRepository reservationRepository) {
        this.roomsRepository = roomsRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Retrieves a list of RoomsDTO objects representing all rooms in the database.
     *
     * @return List of RoomsDTO objects.
     */
    public List<RoomsDTO> findRooms() {
        List<Rooms> roomsList = roomsRepository.findAll();
        return roomsList.stream()
                .map(RoomsBuilder::etoRoomsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a room by its ID.
     *
     * @param id The UUID of the room to retrieve.
     * @return The RoomsDTO object representing the room, or null if not found.
     */
    public RoomsDTO findRoomById(UUID id) {
        Optional<Rooms> optionalRooms = roomsRepository.findById(id);
        if (optionalRooms.isPresent()) {
            LOGGER.info("Room with id {} was found in db", id);
            return RoomsBuilder.etoRoomsDTO(optionalRooms.get());
        } else {
            LOGGER.error("Room with id {} was not found in db", id);
            return null;
        }
    }

    /**
     * Inserts a new room into the database.
     *
     * @param roomsDTO The RoomsDTO object to be inserted.
     * @return The generated UUID for the newly inserted room, or null if insertion fails.
     */
    public UUID insertRooms(RoomsDTO roomsDTO) {
        Rooms rooms = RoomsBuilder.stoEntity(roomsDTO);
        rooms = roomsRepository.save(rooms);
        if (rooms != null) {
            LOGGER.info("Room with id {} was inserted in db", rooms.getRoomId());
            return rooms.getRoomId();
        } else {
            LOGGER.error("Failed to insert room into the database");
            return null;
        }
    }

    /**
     * Updates an existing room in the database based on the provided RoomsDTO.
     *
     * @param roomsDTO The updated RoomsDTO object.
     * @return The updated RoomsDTO object if update is successful, otherwise null.
     */
    public RoomsDTO updateRooms(RoomsDTO roomsDTO) {
        Optional<Rooms> optionalRooms = roomsRepository.findById(roomsDTO.getRoomId());
        if (optionalRooms.isPresent()) {
            Rooms existingRoom = optionalRooms.get();
            existingRoom.setRoomCapacity(roomsDTO.getRoomCapacity());
            existingRoom.setRoomDescription(roomsDTO.getRoomDescription());
            existingRoom.setRoomNumber(roomsDTO.getRoomNumber());
            existingRoom.setRoomCost(roomsDTO.getRoomCost());
            existingRoom.setRoomType(roomsDTO.getRoomType());
            existingRoom.setRoomRate(roomsDTO.getRoomRate());

            Rooms updatedRoom = roomsRepository.save(existingRoom);
            LOGGER.info("Room with id {} was updated in db.", existingRoom.getRoomId());
            return RoomsBuilder.etoRoomsDTO(updatedRoom);
        } else {
            LOGGER.error("Room with id {} not found. Update operation aborted.", roomsDTO.getRoomId());
            return null;
        }
    }

    /**
     * Deletes a room from the database based on the provided UUID.
     *
     * @param id The UUID of the room to be deleted.
     */

    @Transactional
    public void deleteRooms(UUID id) {
        Optional<Rooms> optionalRooms = roomsRepository.findById(id);
        if (optionalRooms.isPresent()) {
            Rooms room = optionalRooms.get();
            List<Reservation> reservations = room.getReservations();
            reservationRepository.deleteAll(reservations);
            roomsRepository.delete(room);
        }
    }

}