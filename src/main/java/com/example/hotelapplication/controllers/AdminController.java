package com.example.hotelapplication.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * The AdminController class handles requests related to the admin dashboard.
 * It provides endpoints for accessing and managing admin functionalities.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(path = "/admin")
public class AdminController {

    /**
     * Displays the admin dashboard.
     *
     * @return A ModelAndView object representing the admin dashboard view.
     */
    @GetMapping()
    public ModelAndView home() {
        return new ModelAndView("admin/admin");
    }
}
