package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.services.PersonServices;
import com.example.hotelapplication.services.ReservationServices;
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
 * Controller class for handling Reservation-related operations.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:3000")
@RequestMapping(value = "/reservations")
public class ReservationController {
    private final ReservationServices reservationServices;
    private final PersonServices personServices;
    private final RoomsServices roomsServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

    /**
     * Constructor for ReservationController.
     *
     * @param reservationServices The service for handling Reservation-related operations.
     * @param personServices      The service for handling Person-related operations.
     * @param roomsServices       The service for handling Rooms-related operations.
     */
    public ReservationController(ReservationServices reservationServices, PersonServices personServices, RoomsServices roomsServices) {
        this.reservationServices = reservationServices;
        this.personServices = personServices;
        this.roomsServices = roomsServices;
    }

    /**
     * Retrieves a list of all reservations.
     *
     * @return ResponseEntity containing the list of ReservationDTOs and HttpStatus OK.
     */
    @GetMapping()
    public ModelAndView getReservations() {
        List<ReservationDTO> reservations = reservationServices.findReservations();
        ModelAndView modelAndView = new ModelAndView("reservations");
        modelAndView.addObject("reservations", reservations);
        return modelAndView;
    }

    /**
     * Retrieves a specific reservation by ID.
     *
     * @param reservationId The UUID of the reservation to retrieve.
     * @return ResponseEntity containing the ReservationDTO if found, otherwise HttpStatus NOT_FOUND.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable("id") UUID reservationId) {
        ReservationDTO dto = reservationServices.findReservationsById(reservationId);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @return ResponseEntity containing the generated UUID for the newly inserted reservation and HttpStatus CREATED,
     * or HttpStatus.BAD_REQUEST if insertion fails.
     */
    @PostMapping()
    public ResponseEntity<UUID> insertReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        UUID reservationID = reservationServices.insertReservations(reservationDTO);
        if (reservationID != null) {
            return new ResponseEntity<>(reservationID, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing reservation in the database.
     *
     * @param reservationDTO The updated ReservationDTO object.
     * @return ResponseEntity with a success message and HttpStatus OK if update is successful,
     * or HttpStatus.NOT_FOUND if the reservation is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable("id") UUID id, @Valid @RequestBody ReservationDTO reservationDTO) {
        reservationDTO.setReservationId(id);
        ReservationDTO updatedReservationDTO = reservationServices.updateReservations(reservationDTO);
        if (updatedReservationDTO != null) {
            return new ResponseEntity<>("Reservation with the id = " + reservationDTO.getReservationId() + " was successfully updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a reservation from the database.
     *
     * @param id The UUID of the reservation to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK if deletion is successful,
     * or HttpStatus.NOT_FOUND if the reservation is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable("id") UUID id) {
        reservationServices.deleteReservations(id);
        return new ResponseEntity<>("Reservation successfully deleted!", HttpStatus.OK);
    }
}
