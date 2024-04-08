package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.services.PersonServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * The MainController class handles requests related to the main functionalities of the hotel application.
 * It provides endpoints for home page display and user authentication.
 */
@Controller
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/")
public class MainController {

    private final PersonServices personServices;

    /**
     * Constructs a new instance of MainController with the specified PersonServices.
     *
     * @param personServices The service for managing person-related operations.
     */
    public MainController(PersonServices personServices) {
        this.personServices = personServices;
    }

    /**
     * Displays the home page.
     *
     * @param request The HttpServletRequest object representing the HTTP request.
     * @return A ModelAndView object representing the home page view.
     */
    @GetMapping()
    public ModelAndView home(HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        return new ModelAndView("start");
    }

    /**
     * Handles user login authentication.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @param request  The HttpServletRequest object representing the HTTP request.
     * @return A ModelAndView object representing the appropriate redirect view based on user role.
     */
    @PostMapping("")
    public ModelAndView login(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        PersonDTO authenticatedPerson = personServices.authenticate(email, password);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("person", authenticatedPerson);
        if (authenticatedPerson != null) {
            if (authenticatedPerson.getRole().equals(RoleType.ADMIN)) {
                modelAndView.setViewName("redirect:/admin");
            } else if (authenticatedPerson.getRole().equals(RoleType.CLIENT)) {
                modelAndView.setViewName("redirect:/home/" + authenticatedPerson.getId());
            } else {
                modelAndView.setViewName("errorPage");
            }
            request.getSession().setAttribute("authenticatedPerson", authenticatedPerson);
            System.out.println(request.getSession().getAttribute("authenticatedPerson"));
            return modelAndView;
        } else {
            // Return error response if authentication fails
            return new ModelAndView("errorPage");
        }
    }
}
