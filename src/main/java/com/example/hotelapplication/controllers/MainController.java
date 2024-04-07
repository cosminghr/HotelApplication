package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.services.PersonServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/")
public class MainController {
    private final PersonServices personServices;

    public MainController(PersonServices personServices) {
        this.personServices = personServices;
    }

    @GetMapping()
    public ModelAndView home() {
        return new ModelAndView("start");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("start");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        PersonDTO authenticatedPerson = personServices.authenticate(email, password);
        if (authenticatedPerson != null) {
            // Return user details if authentication is successful
            return ResponseEntity.ok(authenticatedPerson);
        } else {
            // Return error response if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}
