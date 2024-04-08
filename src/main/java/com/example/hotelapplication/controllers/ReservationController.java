package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.services.ReservationServices;
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
import java.util.Optional;
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
     * @param reservationServices The service for handling Reservation-related operations.
     * @param personRepository
     */
    public ReservationController(ReservationServices reservationServices, PersonRepository personRepository) {
        this.reservationServices = reservationServices;
        this.personRepository = personRepository;
    }



    /**
     * Retrieves a list of all reservations.
     *
     * @return ResponseEntity containing the list of ReservationDTOs and HttpStatus OK.
     */
    @GetMapping("/all")
    public ModelAndView getReservations() {
        List<ReservationDTO> reservations = reservationServices.findReservations();
        ModelAndView modelAndView = new ModelAndView("reservations");
        modelAndView.addObject("reservations", reservations);
        return modelAndView;
    }

    @GetMapping("/userReservations")
    public ModelAndView getReservationsUser( HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        List<ReservationDTO> reservations = reservationServices.findReservationsByPerson(authenticatedPerson.getId());
        List<ReservationDTO> newReservations = new ArrayList<>();
        for(ReservationDTO reservationDTO: reservations){
            if(reservationDTO.getPerson().getId().equals(authenticatedPerson.getId())){
                newReservations.add(reservationDTO);
            }
        }
        ModelAndView modelAndView = new ModelAndView("userReservations");
        modelAndView.addObject("reservations", newReservations);
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

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id){
        ReservationDTO reservation = reservationServices.findReservationsById(id);
        PersonDTO person = reservation.getPerson();
        List<RoomsDTO> rooms = reservation.getRooms();
        List<RoomsDTO> allRooms = reservationServices.findAllRooms();
        ModelAndView modelAndView = new ModelAndView("editReservations");
        modelAndView.addObject("reservation", reservation);
        modelAndView.addObject("rooms", rooms);
        modelAndView.addObject("person", person);
        modelAndView.addObject("allRooms", allRooms);
        return modelAndView;
    }

    @GetMapping("/createReservations")
    public ModelAndView create() {
        List<ReservationDTO> reservation = reservationServices.findReservations();
        List<RoomsDTO> rooms = reservationServices.findAllRooms();
        List<PersonDTO> persons = reservationServices.findAllPersons(); // Fetch all persons from the repository
        ModelAndView modelAndView = new ModelAndView("createReservations");
        modelAndView.addObject("persons", persons); // Add the list of persons to the model
        modelAndView.addObject("rooms", rooms);
        modelAndView.addObject("reservation", reservation);
        return modelAndView;
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservationDTO The ReservationDTO object to be inserted.
     * @return ResponseEntity containing the generated UUID for the newly inserted reservation and HttpStatus CREATED,
     * or HttpStatus.BAD_REQUEST if insertion fails.
     */
    @PostMapping("/create")
    public ModelAndView insertReservation(@ModelAttribute ReservationDTO reservationDTO, @RequestParam("personId") UUID idPerson, @RequestParam("roomIds") List<UUID> idRooms) {
        PersonDTO personDTO = reservationServices.findPersonByIdInReservation(idPerson);
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for(UUID roomId : idRooms){
            RoomsDTO roomsDTO = reservationServices.findRoomByIdInReservation(roomId);
            roomsDTOs.add(roomsDTO);
        }
        reservationDTO.setRooms(roomsDTOs);
        reservationDTO.setPerson(personDTO);
        reservationServices.insertReservations(reservationDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/reservations/all");
        return modelAndView;
    }

    @PostMapping("/createReservationUser")
    public ModelAndView insertReservationUser(@ModelAttribute ReservationDTO reservationDTO,
                                              @RequestParam("roomIds") List<UUID> idRooms,
                                              @RequestParam("startDate")LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate,
                                              HttpServletRequest request){
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO personDTO = reservationServices.findPersonByIdInReservation(authenticatedPerson.getId());
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for(UUID roomId : idRooms){
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
     * @param reservationDTO The updated ReservationDTO object.
     * @return ResponseEntity with a success message and HttpStatus OK if update is successful,
     * or HttpStatus.NOT_FOUND if the reservation is not found.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updateReservation(@PathVariable("id") UUID id, @ModelAttribute("reservationDTO") ReservationDTO reservationDTO, @RequestParam("roomIds") List<UUID> idRooms, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        reservationDTO.setReservationId(id);
        List<RoomsDTO> roomsDTOs = new ArrayList<>();
        for (UUID roomId : idRooms) {
            RoomsDTO roomsDTO = reservationServices.findRoomByIdInReservation(roomId);
            roomsDTOs.add(roomsDTO);
        }
        reservationDTO.setRooms(roomsDTOs);
        ReservationDTO updatedReservationDTO = reservationServices.updateReservations(reservationDTO);
        ModelAndView modelAndView = new ModelAndView();
        if (updatedReservationDTO != null) {
            modelAndView.addObject("message", "Reservation with the id = " + reservationDTO.getReservationId() + " was successfully updated.");
            if(authenticatedPerson.getRole()== RoleType.ADMIN){
                modelAndView.setViewName("redirect:/reservations/all"); // Set the name of the success view
            }else if(authenticatedPerson.getRole() == RoleType.CLIENT){
                modelAndView.setViewName("redirect:/reservations/userReservations");
            }else{
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
     * @param id The UUID of the reservation to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK if deletion is successful,
     * or HttpStatus.NOT_FOUND if the reservation is not found.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteReservation(@PathVariable("id") UUID id, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        reservationServices.deleteReservations(id);
        ModelAndView modelAndView = new ModelAndView();
        if(authenticatedPerson.getRole()== RoleType.ADMIN){
            modelAndView.setViewName("redirect:/reservations/all"); // Set the name of the success view
        }else if(authenticatedPerson.getRole() == RoleType.CLIENT){
            modelAndView.setViewName("redirect:/reservations/userReservations");
        }else{
            modelAndView.setViewName("redirect:/errorPage");
        }
        return modelAndView;
    }



}
