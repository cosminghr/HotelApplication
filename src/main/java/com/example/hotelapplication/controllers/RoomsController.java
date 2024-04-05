package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.dtos.ReservationDTO;
import com.example.hotelapplication.dtos.RoomsDTO;
import com.example.hotelapplication.services.RoomsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
     * @return ResponseEntity containing the list of RoomsDTOs and HttpStatus OK.
     */
    @GetMapping("/all")
    public ModelAndView getRooms() {
        List<RoomsDTO> rooms = roomsServices.findRooms();
        ModelAndView modelAndView = new ModelAndView("rooms");
        modelAndView.addObject("rooms", rooms);
        for(RoomsDTO roomsDTO:rooms){
            List<ReservationDTO> reservationDTOS = roomsDTO.getReservationDTOS();
            modelAndView.addObject("reservation", reservationDTOS);
        }
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

    @GetMapping("/createRooms")
    public ModelAndView create(){
        return new ModelAndView("createRooms");
    }
    @PostMapping("/create")
    public ModelAndView insertRoom(@ModelAttribute RoomsDTO roomsDTO) {
        UUID roomId = roomsServices.insertRooms(roomsDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id){
        RoomsDTO room = roomsServices.findRoomById(id);
        ModelAndView modelAndView = new ModelAndView("editRooms");
        modelAndView.addObject("room", room);
        return modelAndView;
    }
    @PostMapping("/edit/{id}")
    public ModelAndView updateRoom(@PathVariable("id") UUID roomId, @ModelAttribute RoomsDTO roomsDTO) {
        roomsDTO.setRoomId(roomId);
        RoomsDTO updatedRoomsDTO = roomsServices.updateRooms(roomsDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }

    /**
     * Deletes a room from the database.
     *
     * @param roomId The UUID of the room to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK, or NOT_FOUND if the room is not found.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deleteRoom(@PathVariable("id") UUID roomId) {
        roomsServices.deleteRooms(roomId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/rooms/all");
        return modelAndView;
    }
}
