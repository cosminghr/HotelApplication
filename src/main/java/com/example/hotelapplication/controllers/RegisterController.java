package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.services.PersonServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller class for handling user registration.
 */
@Controller
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/")
public class RegisterController {
    private final PersonServices personServices;

    /**
     * Constructor for RegisterController.
     *
     * @param personServices The service for handling user-related operations.
     */
    public RegisterController(PersonServices personServices) {
        this.personServices = personServices;
    }

    /**
     * Retrieves the registration form view.
     *
     * @return ModelAndView representing the registration form view.
     */
    @GetMapping("/register")
    public ModelAndView registerForm() {
        return new ModelAndView("login/register");
    }

    /**
     * Handles user registration.
     *
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password of the user.
     * @param address  The address of the user.
     * @param date     The date of birth of the user.
     * @return ModelAndView for redirecting to the home page after registration.
     */
    @PostMapping("/register")
    public ModelAndView register(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String address,
                                 @RequestParam LocalDate date) {
        // Validate the input data
        if (name == null || email == null || password == null || address == null || date == null) {
            return new ModelAndView("/login/register");
        }

        // Create a new PersonDTO for registration
        PersonDTO personDTO = new PersonDTO();
        personDTO.setName(name);
        personDTO.setEmail(email);
        personDTO.setPassword(password);
        personDTO.setAddress(address);
        personDTO.setDate(date);
        personDTO.setRole(RoleType.CLIENT); // Set the role to CLIENT

        try{
            UUID personId = personServices.insertPerson(personDTO);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/");
            personServices.sendEmailToUser(personDTO, "Register Completed", "You have successfully registered to Mountain Hotel!");
            return modelAndView;
        }catch (Exception e){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        // Insert the person into the database

    }


}
