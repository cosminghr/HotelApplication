package com.example.hotelapplication.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/admin")
public class AdminController {
    @GetMapping()
    public ModelAndView home() {
        return new ModelAndView("/admin");
    }

}
