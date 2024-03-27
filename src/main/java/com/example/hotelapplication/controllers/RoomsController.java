package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.services.RoomsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling Rooms-related operations.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:3000")
@RequestMapping(value = "/rooms")
public class RoomsController {
    private final RoomsServices roomsServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsController.class);

    /**
     * Constructor for RoomsController.
     *
     * @param roomsServices The service for handling Rooms-related operations.
     */
    public RoomsController(RoomsServices roomsServices) {
        this.roomsServices = roomsServices;
    }

    /**
     * Retrieves a list of RoomsDTO objects representing all rooms.
     *
     * @return ResponseEntity containing the list of RoomsDTOs and HttpStatus OK.
     */
    @GetMapping()
    public ModelAndView getRooms() {
        List<RoomsDTO> rooms = roomsServices.findRooms();
        ModelAndView modelAndView = new ModelAndView("rooms");
        modelAndView.addObject("rooms", rooms);
        return modelAndView;
    }

    /**
     * Retrieves a room by its ID.
     *
     * @param roomId The UUID of the room to retrieve.
     * @return ResponseEntity containing the RoomsDTO object and HttpStatus OK, or NOT_FOUND if the room is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomsDTO> getRoomById(@PathVariable("id") UUID roomId) {
        RoomsDTO dto = roomsServices.findRoomById(roomId);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            LOGGER.error("Room with id {} not found.", roomId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Inserts a new room into the database.
     *
     * @param roomsDTO The RoomsDTO object to be inserted.
     * @return ResponseEntity containing the generated UUID for the newly inserted room and HttpStatus CREATED.
     */
    @PostMapping()
    public ResponseEntity<UUID> insertRoom(@Valid @RequestBody RoomsDTO roomsDTO) {
        UUID roomId = roomsServices.insertRooms(roomsDTO);
        return new ResponseEntity<>(roomId, HttpStatus.CREATED);
    }

    /**
     * Updates an existing room in the database.
     *
     * @param roomId   The UUID of the room to be updated.
     * @param roomsDTO The updated RoomsDTO object.
     * @return ResponseEntity with a success message and HttpStatus OK, or NOT_FOUND if the room is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoom(@PathVariable("id") UUID roomId, @Valid @RequestBody RoomsDTO roomsDTO) {
        roomsDTO.setRoomId(roomId);
        RoomsDTO updatedRoomsDTO = roomsServices.updateRooms(roomsDTO);
        if (updatedRoomsDTO != null) {
            return new ResponseEntity<>("Room with the id = " + roomId + " was successfully updated!", HttpStatus.OK);
        } else {
            LOGGER.error("Room with id {} not found.", roomId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a room from the database.
     *
     * @param roomId The UUID of the room to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK, or NOT_FOUND if the room is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable("id") UUID roomId) {
        roomsServices.deleteRooms(roomId);
        return new ResponseEntity<>("Room successfully deleted!", HttpStatus.OK);
    }
}
