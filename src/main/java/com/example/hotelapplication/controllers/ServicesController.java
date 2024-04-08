package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.ServicesDTO;
import com.example.hotelapplication.services.ServicesServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling Services-related operations.
 */
@RestController
@RequestMapping(value = "/services")
public class ServicesController {

    private final ServicesServices servicesServices;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesController.class);

    /**
     * Constructor for ServicesController.
     *
     * @param servicesServices The service for handling Services-related operations.
     */
    public ServicesController(ServicesServices servicesServices) {
        this.servicesServices = servicesServices;
    }


    /**
     * Retrieves a list of ServicesDTO objects representing all services.
     *
     * @return ModelAndView containing the list of ServicesDTOs and services view.
     */
    @GetMapping(value = "/all")
    public ModelAndView getServices() {
        List<ServicesDTO> services = servicesServices.findAllServices();
        ModelAndView modelAndView = new ModelAndView("services");
        modelAndView.addObject("services", services);
        return modelAndView;
    }

    /**
     * Retrieves a service by its ID.
     *
     * @param servicesId The UUID of the service to retrieve.
     * @return ResponseEntity containing the ServicesDTO if found, or HttpStatus NOT_FOUND if not found.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable("id") UUID servicesId) {
        ServicesDTO dto = servicesServices.findServiceById(servicesId);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Retrieves the create service view.
     *
     * @return ModelAndView containing the createServices view.
     */
    @GetMapping("/createServices")
    public ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("createServices");
        return modelAndView;
    }

    /**
     * Inserts a new service into the database.
     *
     * @param servicesDTO The ServicesDTO object to be inserted.
     * @return ModelAndView for redirecting to the services/all page after insertion.
     */
    @PostMapping("/create")
    public ModelAndView insertService(@ModelAttribute ServicesDTO servicesDTO) {
        UUID serviceID = servicesServices.insertService(servicesDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/services/all");
        return modelAndView;
    }

    /**
     * Retrieves the edit service view with data pre-populated for editing.
     *
     * @param id The UUID of the service to edit.
     * @return ModelAndView containing the editServices view with pre-populated data.
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id) {
        ServicesDTO servicesDTO = servicesServices.findServiceById(id);
        ModelAndView modelAndView = new ModelAndView("editServices");
        modelAndView.addObject("service", servicesDTO);
        return modelAndView;
    }

    /**
     * Updates an existing service in the database.
     *
     * @param id          The UUID of the service to be updated.
     * @param servicesDTO The updated ServicesDTO object.
     * @return ModelAndView for redirecting to the services/all page after update.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updateService(@PathVariable("id") UUID id, @ModelAttribute ServicesDTO servicesDTO) {
        servicesDTO.setServiceId(id);
        ServicesDTO updatedServiceDTO = servicesServices.updateService(servicesDTO);
        ModelAndView modelAndView = new ModelAndView("redirect:/services/all");
        return modelAndView;
    }

    /**
     * Deletes a service from the database.
     *
     * @param id The UUID of the service to be deleted.
     * @return ModelAndView for redirecting to the services/all page after deletion.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteService(@PathVariable("id") UUID id) {
        servicesServices.deleteService(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/services/all");
        return modelAndView;
    }
}
