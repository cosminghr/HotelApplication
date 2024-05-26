package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.exceptions.EmailSendingException;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.services.ReservationServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling Reservation-related operations.
 */
@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {
    private final ReservationServices reservationServices;
    private final PersonRepository personRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

    /**
     * Constructor for ReservationController.
     *
     * @param reservationServices            The service for handling Reservation-related operations.
     * @param personRepository               The repository for accessing person data.
     */
    public ReservationController(ReservationServices reservationServices, PersonRepository personRepository) {
        this.reservationServices = reservationServices;
        this.personRepository = personRepository;
    }

    /**
     * Retrieves a list of all reservations.
     *
     * @return ModelAndView containing the list of ReservationDTOs and reservations view.
     */
    @GetMapping("/all")
    public ModelAndView getReservations() {
        List<ReservationDTO> reservations = reservationServices.findReservations();
        ModelAndView modelAndView = new ModelAndView("/admin/reservations/reservations");
        modelAndView.addObject("reservations", reservations);
        return modelAndView;
    }

    /**
     * Retrieves a list of reservations for the authenticated user.
     *
     * @param request The HTTP servlet request.
     * @return ModelAndView containing the list of ReservationDTOs and userReservations view.
     */
    @GetMapping("/userReservations")
    public ModelAndView getReservationsUser( HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        List<ReservationDTO> reservations = reservationServices.findReservations();
        List<ReservationDTO> newReservations = new ArrayList<>();
        for (ReservationDTO reservationDTO : reservations) {
            if (reservationDTO.getPerson().getId().equals(authenticatedPerson.getId())) {
                newReservations.add(reservationDTO);
            }
        }
        if(authenticatedPerson.getRole().equals(RoleType.ADMIN)){
            ModelAndView modelAndView = new ModelAndView("errorPage");
            return modelAndView;
        }else{
            ModelAndView modelAndView = new ModelAndView("client/userReservations");
            modelAndView.addObject("reservations", newReservations);
            return modelAndView;
        }
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
     * Retrieves the edit reservation view with data pre-populated for editing.
     *
     * @param id The UUID of the reservation to edit.
     * @return ModelAndView containing the editReservations view with pre-populated data.
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id) {
        ReservationDTO reservation = reservationServices.findReservationsById(id);
        PersonDTO person = reservation.getPerson();
        List<RoomsDTO> rooms = reservation.getRooms();
        List<RoomsDTO> allRooms = reservationServices.findAllRooms();
        ModelAndView modelAndView = new ModelAndView("/admin/reservations/editReservations");
        modelAndView.addObject("reservation", reservation);
        modelAndView.addObject("rooms", rooms);
        modelAndView.addObject("person", person);
        modelAndView.addObject("allRooms", allRooms);
        return modelAndView;
    }

    /**
     * Retrieves the create reservation view.
     *
     * @return ModelAndView containing the createReservations view with necessary data.
     */
    @GetMapping("/createReservations")
    public ModelAndView create() {
        List<ReservationDTO> reservation = reservationServices.findReservations();
        List<RoomsDTO> rooms = reservationServices.findAllRooms();
        List<PersonDTO> persons = reservationServices.findAllPersons();
        ModelAndView modelAndView = new ModelAndView("/admin/reservations/createReservations");
        modelAndView.addObject("persons", persons);
        modelAndView.addObject("rooms", rooms);
        modelAndView.addObject("reservation", reservation);
        return modelAndView;
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @param idPerson       The UUID of the person making the reservation.
     * @param idRooms        The UUIDs of the rooms booked in the reservation.
     * @return ModelAndView for redirecting to the reservations/all page after insertion.
     */
    @PostMapping("/create")
    public ModelAndView insertReservation(@ModelAttribute ReservationDTO reservationDTO,
                                          @RequestParam("personId") UUID idPerson,
                                          @RequestParam("roomIds") List<UUID> idRooms) throws JsonProcessingException, EmailSendingException {
        PersonDTO personDTO = reservationServices.findPersonByIdInReservation(idPerson);
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for (UUID roomId : idRooms) {
            RoomsDTO roomsDTO = reservationServices.findRoomByIdInReservation(roomId);
            roomsDTOs.add(roomsDTO);
        }
        reservationDTO.setRooms(roomsDTOs);
        reservationDTO.setPerson(personDTO);
        UUID reservationId = reservationServices.insertReservations(reservationDTO);
        reservationDTO.setReservationId(reservationId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/reservations/all");
        reservationServices.emailCompose(reservationDTO);
        return modelAndView;
    }

    /**
     * Inserts a new reservation for the authenticated user into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @param idRooms        The UUIDs of the rooms booked in the reservation.
     * @param startDate      The start date of the reservation.
     * @param endDate        The end date of the reservation.
     * @param request        The HTTP servlet request.
     * @return ModelAndView for redirecting to the reservations/userReservations page after insertion.
     */
    @PostMapping("/createReservationUser")
    public ModelAndView insertReservationUser(@ModelAttribute ReservationDTO reservationDTO,
                                              @RequestParam("roomIds") List<UUID> idRooms,
                                              @RequestParam("startDate") LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate,
                                              HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO personDTO = reservationServices.findPersonByIdInReservation(authenticatedPerson.getId());
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for (UUID roomId : idRooms) {
            RoomsDTO roomsDTO = reservationServices.findRoomByIdInReservation(roomId);
            roomsDTOs.add(roomsDTO);
        }
        reservationDTO.setRooms(roomsDTOs);
        reservationDTO.setPerson(personDTO);
        reservationDTO.setReservationStart(startDate);
        reservationDTO.setReservationEnd(endDate);
        reservationServices.insertReservations(reservationDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/reservations/userReservations");
        return modelAndView;
    }

    /**
     * Updates an existing reservation in the database.
     *
     * @param id            The UUID of the reservation to be updated.
     * @param reservationDTO The updated ReservationDTO object.
     * @param idRooms       The UUIDs of the rooms to be updated in the reservation.
     * @param request       The HTTP servlet request.
     * @return ModelAndView for redirecting to the appropriate page based on the role after update.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updateReservation(@PathVariable("id") UUID id,
                                          @ModelAttribute("reservationDTO") ReservationDTO reservationDTO,
                                          @RequestParam("roomIds") List<UUID> idRooms,
                                          HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        reservationDTO.setReservationId(id);
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for (UUID roomId : idRooms) {
            RoomsDTO roomsDTO = reservationServices.findRoomByIdInReservation(roomId);
            roomsDTOs.add(roomsDTO);
        }
        reservationDTO.setPerson(authenticatedPerson);
        reservationDTO.setRooms(roomsDTOs);
        ReservationDTO updatedReservationDTO = reservationServices.updateReservations(reservationDTO);
        ModelAndView modelAndView = new ModelAndView();
        if (updatedReservationDTO != null) {
            modelAndView.addObject("message", "Reservation with the id = " + reservationDTO.getReservationId() + " was successfully updated.");
            if (authenticatedPerson.getRole() == RoleType.ADMIN) {
                modelAndView.setViewName("redirect:/reservations/all");
            } else if (authenticatedPerson.getRole() == RoleType.CLIENT) {
                modelAndView.setViewName("redirect:/reservations/userReservations");
            } else {
                modelAndView.setViewName("redirect:/errorPage");
            }
        } else {
            modelAndView.setViewName("redirect:/errorPage");
        }
        return modelAndView;
    }

    /**
     * Deletes a reservation from the database.
     *
     * @param id      The UUID of the reservation to be deleted.
     * @param request The HTTP servlet request.
     * @return ModelAndView for redirecting to the appropriate page based on the role after deletion.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteReservation(@PathVariable("id") UUID id, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        reservationServices.deleteReservations(id);
        ModelAndView modelAndView = new ModelAndView();
        if (authenticatedPerson.getRole() == RoleType.ADMIN) {
            modelAndView.setViewName("redirect:/reservations/all");
        } else if (authenticatedPerson.getRole() == RoleType.CLIENT) {
            modelAndView.setViewName("redirect:/reservations/userReservations");
        } else {
            modelAndView.setViewName("redirect:/errorPage");
        }
        return modelAndView;
    }

}
