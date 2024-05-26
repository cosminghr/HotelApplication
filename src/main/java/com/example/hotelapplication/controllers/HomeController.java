package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.services.RoomsServices;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The HomeController class handles requests related to the home page of the hotel application.
 * It provides endpoints for displaying the home page with available rooms.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/home")
public class HomeController {

    private final RoomsServices roomsServices;
    private final PersonRepository personRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsController.class);

    /**
     * Constructs a new instance of HomeController with the specified RoomsServices and PersonRepository.
     *
     * @param roomsServices    The service for managing room-related operations.
     * @param personRepository The repository for accessing person-related data.
     */
    @Autowired
    public HomeController(RoomsServices roomsServices, PersonRepository personRepository) {
        this.roomsServices = roomsServices;
        this.personRepository = personRepository;
    }

    /**
     * Displays the home page with available rooms for the specified person.
     *
     * @param personId The UUID of the person for whom the home page is being accessed.
     * @return A ModelAndView object representing the home page view.
     */
    @GetMapping("/{id}")
    public ModelAndView home(@PathVariable("id") UUID personId, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        List<RoomsDTO> rooms = roomsServices.findRooms();
        Optional<Person> person = personRepository.findById(personId);
        ModelAndView modelAndView = new ModelAndView("redirect:/home");
        modelAndView.addObject("rooms", rooms); // Add the list of rooms to the model
        modelAndView.addObject("person", person);
        return modelAndView;
    }

    /**
     * Displays the home page with available rooms.
     *
     * @return A ModelAndView object representing the home page view.
     */
    @GetMapping("")
    public ModelAndView home1() {
        ModelAndView modelAndView = new ModelAndView("/client/home");
        List<RoomsDTO> rooms = roomsServices.findRooms();
        modelAndView.addObject("rooms", rooms); // Add the list of rooms to the model
        return modelAndView;
    }

}

