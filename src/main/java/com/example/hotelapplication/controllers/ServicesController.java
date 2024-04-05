package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.ServicesDTO;
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
@CrossOrigin(originPatterns = "http://localhost:8080")
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
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("services", services);
        for(ServicesDTO servicesDTO: services){
            List<RoomsDTO> roomsDTOS = servicesDTO.getRoomsDTOS();
            modelAndView.addObject("rooms", roomsDTOS);
        }
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
     * Inserts a new service into the database.
     *
     * @param servicesDTO The ServicesDTO object to be inserted.
     * @return ResponseEntity containing the generated UUID for the newly inserted service and HttpStatus CREATED.
     */
    @PostMapping()
    public ResponseEntity<UUID> insertService(@Valid @RequestBody ServicesDTO servicesDTO) {
        UUID serviceID = servicesServices.insertService(servicesDTO);
        return new ResponseEntity<>(serviceID, HttpStatus.CREATED);
    }

    /**
     * Updates an existing service in the database.
     *
     * @param id           The UUID of the service to be updated.
     * @param servicesDTO  The updated ServicesDTO object.
     * @return ResponseEntity with a success message and HttpStatus OK, or HttpStatus.NOT_FOUND if the service is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable("id") UUID id, @Valid @RequestBody ServicesDTO servicesDTO) {
        servicesDTO.setServiceId(id);
        ServicesDTO updatedServiceDTO = servicesServices.updateService(servicesDTO);
        if (updatedServiceDTO != null) {
            return new ResponseEntity<>("Service with the id = " + servicesDTO.getServiceId() + " was successfully updated!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a service from the database.
     *
     * @param id The UUID of the service to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK, or HttpStatus.NOT_FOUND if the service is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable("id") UUID id) {
        servicesServices.deleteService(id);
        return new ResponseEntity<>("Service successfully deleted!", HttpStatus.OK);
    }
}
