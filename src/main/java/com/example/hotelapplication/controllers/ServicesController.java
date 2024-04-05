package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.ServicesDTO;
import com.example.hotelapplication.repositories.RoomsRepository;
import com.example.hotelapplication.services.ServicesServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
     * @return ResponseEntity containing the list of ServicesDTOs and HttpStatus OK.
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


    @GetMapping("/createServices")
    public ModelAndView create(){
        ModelAndView modelAndView = new ModelAndView("createServices");
        return modelAndView;
    }
    @PostMapping("/create")
    public ModelAndView insertService(@ModelAttribute ServicesDTO servicesDTO) {
        UUID serviceID = servicesServices.insertService(servicesDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/services/all");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id){
        ServicesDTO servicesDTO = servicesServices.findServiceById(id);
        ModelAndView modelAndView = new ModelAndView("editServices");
        modelAndView.addObject("service", servicesDTO);
        return modelAndView;
    }

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
     * @return ResponseEntity with a success message and HttpStatus OK, or HttpStatus.NOT_FOUND if the service is not found.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteService(@PathVariable("id") UUID id) {
        servicesServices.deleteService(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/services/all");
        return modelAndView;
    }
}
