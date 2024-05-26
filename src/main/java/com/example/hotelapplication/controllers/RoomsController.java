package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.dtos.ServicesDTO;
import com.example.hotelapplication.services.RoomsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling Rooms-related operations.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(value = "/rooms")
public class RoomsController {
    private final RoomsServices roomsServices;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsController.class);

    /**
     * Constructor for RoomsController.
     *
     * @param roomsServices The service for handling Rooms-related operations.
     */
    public RoomsController(RoomsServices roomsServices) {
        this.roomsServices = roomsServices;
    }

    /**
     * Retrieves a list of RoomsDTO objects representing all rooms.
     *
     * @return ModelAndView containing the list of RoomsDTOs and rooms view.
     */
    @GetMapping("/all")
    public ModelAndView getRooms() {
        List<RoomsDTO> rooms = roomsServices.findRooms();
        ModelAndView modelAndView = new ModelAndView("/admin/rooms/rooms");
        modelAndView.addObject("rooms", rooms);
        return modelAndView;
    }

    /**
     * Retrieves a list of RoomsDTO objects representing all rooms for user.
     *
     * @return ModelAndView containing the list of RoomsDTOs and bookRooms view.
     */
    @GetMapping("/allRooms")
    public ModelAndView getRoomsUser() {
        List<RoomsDTO> rooms = roomsServices.findRooms();
        ModelAndView modelAndView = new ModelAndView("/client/bookRooms");
        modelAndView.addObject("rooms", rooms);
        return modelAndView;
    }

    /**
     * Retrieves a room by its ID.
     *
     * @param roomId The UUID of the room to retrieve.
     * @return ResponseEntity containing the RoomsDTO object and HttpStatus OK, or NOT_FOUND if the room is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomsDTO> getRoomById(@PathVariable("id") UUID roomId) {
        RoomsDTO dto = roomsServices.findRoomById(roomId);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            LOGGER.error("Room with id {} not found.", roomId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the create room view.
     *
     * @return ModelAndView containing the createRooms view with necessary data.
     */
    @GetMapping("/createRooms")
    public ModelAndView create() {
        List<ServicesDTO> services = roomsServices.findAllServices();
        ModelAndView modelAndView = new ModelAndView("/admin/rooms/createRooms");
        modelAndView.addObject("services", services);
        return modelAndView;
    }

    /**
     * Inserts a new room into the database.
     *
     * @param roomsDTO   The RoomsDTO object to be inserted.
     * @param idServices The UUIDs of the services provided in the room.
     * @return ModelAndView for redirecting to the rooms/all page after insertion.
     */
    @PostMapping("/create")
    public ModelAndView insertRoom(@ModelAttribute RoomsDTO roomsDTO, @RequestParam("servicesIds") List<UUID> idServices) {
        List<ServicesDTO> servicesDTOS = new ArrayList<>();
        for (UUID serviceId : idServices) {
            ServicesDTO servicesDTO = roomsServices.findServiceByIdInRoom(serviceId);
            servicesDTOS.add(servicesDTO);
        }
        roomsDTO.setServices(servicesDTOS);
        roomsServices.insertRooms(roomsDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }

    /**
     * Retrieves the edit room view with data pre-populated for editing.
     *
     * @param id The UUID of the room to edit.
     * @return ModelAndView containing the editRooms view with pre-populated data.
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id) {
        RoomsDTO room = roomsServices.findRoomById(id);
        List<ServicesDTO> services = room.getServices();
        List<ServicesDTO> allServices = roomsServices.findAllServices();
        ModelAndView modelAndView = new ModelAndView("/admin/rooms/editRooms");
        modelAndView.addObject("room", room);
        modelAndView.addObject("services", services);
        modelAndView.addObject("allServices", allServices);
        return modelAndView;
    }

    /**
     * Updates an existing room in the database.
     *
     * @param roomId     The UUID of the room to be updated.
     * @param roomsDTO   The updated RoomsDTO object.
     * @param idServices The UUIDs of the services provided in the room.
     * @return ModelAndView for redirecting to the rooms/all page after update.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updateRoom(@PathVariable("id") UUID roomId, @ModelAttribute RoomsDTO roomsDTO, @RequestParam("servicesIds") List<UUID> idServices) {
        roomsDTO.setRoomId(roomId);
        List<ServicesDTO> servicesDTOS = new ArrayList<>();
        for (UUID serviceId : idServices) {
            ServicesDTO servicesDTO = roomsServices.findServiceByIdInRoom(serviceId);
            servicesDTOS.add(servicesDTO);
        }
        roomsDTO.setServices(servicesDTOS);
        RoomsDTO updatedRoomsDTO = roomsServices.updateRooms(roomsDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }

    /**
     * Deletes a room from the database.
     *
     * @param roomId The UUID of the room to be deleted.
     * @return ModelAndView for redirecting to the rooms/all page after deletion.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteRoom(@PathVariable("id") UUID roomId) {
        roomsServices.deleteRooms(roomId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }
}
