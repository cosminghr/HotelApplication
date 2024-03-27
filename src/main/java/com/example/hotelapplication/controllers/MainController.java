package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/")
public class MainController {
    @GetMapping()
    public ModelAndView home() {
        return new ModelAndView("start");
    }

}
