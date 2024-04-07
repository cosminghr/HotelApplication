package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.services.PersonServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Controller
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/")
public class RegisterController {
    private final PersonServices personServices;

    public RegisterController(PersonServices personServices) {
        this.personServices = personServices;
    }

    @GetMapping("/register")
    public ModelAndView registerForm() {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String address,
                                      @RequestParam LocalDate date) {
        // Validate the input data
        if (name == null || email == null || password == null || address == null || date == null) {
            return new ModelAndView("register");
        }

        // Parse the date string to LocalDate

        // Create a new PersonDTO for registration
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName(name);
        personDTO.setEmail(email);
        personDTO.setPassword(password);
        personDTO.setAddress(address);
        personDTO.setDate(date);
        personDTO.setRole(RoleType.CLIENT); // Set the role to CLIENT

        // Insert the person into the database
        UUID personId = personServices.insertPerson(personDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }
}
