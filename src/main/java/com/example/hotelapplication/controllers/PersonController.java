package com.example.hotelapplication.controllers;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.enums.RoleType;
import com.example.hotelapplication.services.PersonServices;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
     * @return ModelAndView containing the list of PersonDTOs and the view name "person".
     */
    @GetMapping(value = "/all")
    public ModelAndView getPersons(HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());
        if(person.getRole().equals(RoleType.ADMIN)){
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
        }else{
            System.out.println("intru aici");
            ModelAndView modelAndView = new ModelAndView("errorPage");
            return modelAndView;
        }

    }

    /**
     * Retrieves the profile of a specific person.
     *
     * @param request The HttpServletRequest object representing the HTTP request.
     * @return ModelAndView representing the user profile view.
     */
    @GetMapping(value = "/userProfile")
    public ModelAndView getPersonProfile(HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());
        ModelAndView modelAndView = new ModelAndView("userProfile");
        modelAndView.addObject("persons", person);
        return modelAndView;
    }

    /**
     * Retrieves a specific person for editing by ID.
     *
     * @param id The UUID of the person to edit.
     * @return ModelAndView representing the edit person view.
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") UUID id, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());

            PersonDTO personEdited = personServices.findPersonById(id);
            ModelAndView modelAndView = new ModelAndView("editPerson");
            modelAndView.addObject("person", personEdited);
            return modelAndView;



    }

    /**
     * Retrieves the view for creating a new person.
     *
     * @return ModelAndView representing the create person view.
     */
    @GetMapping("/createPerson")
    public ModelAndView create() {
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
     * @return ModelAndView for redirecting to the list of all persons.
     */
    @PostMapping("/create")
    public ModelAndView insertPerson(@ModelAttribute PersonDTO personDTO, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());
        if(person.getRole().equals(RoleType.ADMIN)){
            try{
                personServices.insertPerson(personDTO);
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("redirect:/person/all");
                return modelAndView;
            }catch(DataIntegrityViolationException e){
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("errorPage");
                return modelAndView;
            }

        }else{
            System.out.println("intru aici");
            ModelAndView modelAndView = new ModelAndView("errorPage");
            return modelAndView;
        }

    }

    /**
     * Updates an existing person in the database.
     *
     * @param id        The UUID of the person to be updated.
     * @param personDTO The updated PersonDTO object.
     * @return ModelAndView for redirecting to the list of all persons.
     */
    @PostMapping("/edit/{id}")
    public ModelAndView updatePerson(@PathVariable("id") UUID id, @ModelAttribute PersonDTO personDTO, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());
            personDTO.setId(id);
            PersonDTO updatedPersonDTO = personServices.updatePerson(personDTO);
            ModelAndView modelAndView = new ModelAndView();
            if(person.getRole().equals(RoleType.ADMIN)){
                modelAndView.setViewName("redirect:/person/all");
            }if(person.getRole().equals(RoleType.CLIENT)){
                modelAndView.setViewName("redirect:/person/userProfile");
        }

            return modelAndView;


    }

    /**
     * Deletes a person from the database.
     *
     * @param id The UUID of the person to be deleted.
     * @return ModelAndView for redirecting to the list of all persons.
     */
    @PostMapping("/delete/{id}")
    public ModelAndView deletePerson(@PathVariable("id") UUID id, HttpServletRequest request) {
        PersonDTO authenticatedPerson = (PersonDTO) request.getSession().getAttribute("authenticatedPerson");
        PersonDTO person = personServices.findPersonById(authenticatedPerson.getId());
        ModelAndView modelAndView = new ModelAndView();
        if(person.getRole().equals(RoleType.ADMIN)){
            modelAndView.setViewName("redirect:/person/all");
        }if(person.getRole().equals(RoleType.CLIENT)){
            modelAndView.setViewName("redirect:/");
        }
        personServices.deletePerson(id);



        return modelAndView;
    }
}
