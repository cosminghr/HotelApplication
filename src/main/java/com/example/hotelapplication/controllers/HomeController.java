package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.services.RoomsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/home")
public class HomeController {

    private final RoomsServices roomsServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsController.class);

    @Autowired
    public HomeController(RoomsServices roomsServices) {
        this.roomsServices = roomsServices;
    }

    @GetMapping()
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("/home");
        List<RoomsDTO> rooms = roomsServices.findRooms();
        modelAndView.addObject("rooms", rooms); // Add the list of rooms to the model
        return modelAndView;
    }
}
