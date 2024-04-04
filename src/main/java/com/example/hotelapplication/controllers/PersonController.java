package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.services.PersonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling Person-related operations.
 */
@RestController
@CrossOrigin(originPatterns = "http://localhost:8080")
@RequestMapping(value = "/person")
public class PersonController {

    private final PersonServices personServices;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    /**
     * Constructor for PersonController.
     *
     * @param personServices The service for handling Person-related operations.
     */
    public PersonController(PersonServices personServices) {
        this.personServices = personServices;
    }

    /**
     * Retrieves a list of PersonDTO objects representing all persons.
     *
     * @return ResponseEntity containing the list of PersonDTOs and HttpStatus OK.
     */
    @GetMapping(value = "/all")
    public ModelAndView getPersons() {
        try {
            List<PersonDTO> persons = personServices.findPersons();
            ModelAndView modelAndView = new ModelAndView("person");
            modelAndView.addObject("persons", persons);
            return modelAndView;
        } catch (MethodArgumentTypeMismatchException e) {
            // Handle invalid UUID string error
            // Redirect to an error page or return an error response
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Invalid UUID string");
            return errorModelAndView;
        }
    }
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id){
        PersonDTO person = personServices.findPersonById(id);
        ModelAndView modelAndView = new ModelAndView("editPerson");
        modelAndView.addObject("person", person);
        return modelAndView;
    }

    @GetMapping("/createPerson")
    public ModelAndView create(){
        return new ModelAndView("createPerson");
    }
    /**
     * Retrieves a specific person by ID.
     *
     * @param personId The UUID of the person to retrieve.
     * @return ResponseEntity containing the PersonDTO object and HttpStatus OK if found, else HttpStatus NOT_FOUND.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable("id") UUID personId) {
        PersonDTO dto = personServices.findPersonById(personId);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Inserts a new person into the database.
     *
     * @param personDTO The PersonDTO object to be inserted.
     * @return ResponseEntity containing the generated UUID for the newly inserted person and HttpStatus CREATED.
     */
    @PostMapping("/create")
    public ModelAndView insertPerson(@ModelAttribute PersonDTO personDTO) {
        personServices.insertPerson(personDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/person/all");
        return modelAndView;
    }

    /**
     * Updates an existing person in the database.
     *
     * @param id        The UUID of the person to be updated.
     * @param personDTO The updated PersonDTO object.
     * @return ResponseEntity with a success message and HttpStatus OK if the update is successful, else HttpStatus NOT_FOUND.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updatePerson(@PathVariable("id") UUID id, @ModelAttribute PersonDTO personDTO) {
        personDTO.setId(id);
        PersonDTO updatedPersonDTO = personServices.updatePerson(personDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/person/all");
        return modelAndView;
    }

    /**
     * Deletes a person from the database.
     *
     * @param id The UUID of the person to be deleted.
     * @return ResponseEntity with a success message and HttpStatus OK if the deletion is successful, else HttpStatus NOT_FOUND.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deletePerson(@PathVariable("id") UUID id) {
        personServices.deletePerson(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/person/all");
        return modelAndView;
    }
}
