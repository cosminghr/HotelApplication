package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.services.RoomsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/home")
public class HomeController {

    private final RoomsServices roomsServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsController.class);
    private final PersonRepository personRepository;

    @Autowired
    public HomeController(RoomsServices roomsServices, PersonRepository personRepository) {
        this.roomsServices = roomsServices;
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}")
    public ModelAndView home(@PathVariable("id") UUID personId) {
        List<RoomsDTO> rooms = roomsServices.findRooms();
        Optional<Person> person = personRepository.findById(personId);
        ModelAndView modelAndView = new ModelAndView("redirect:/home");
        modelAndView.addObject("rooms", rooms); // Add the list of rooms to the model
        modelAndView.addObject("person", person);
        return modelAndView;
    }
    @GetMapping("")
    public ModelAndView home1() {

        ModelAndView modelAndView = new ModelAndView("home");
        List<RoomsDTO> rooms = roomsServices.findRooms();
        modelAndView.addObject("rooms", rooms); // Add the list of rooms to the model
        return modelAndView;
    }

}
