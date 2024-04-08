package com.example.hotelapplication.validators;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.exceptions.InvalidEmail;

import static com.example.hotelapplication.constants.PersonConstants.EMAIL_REGEX;

public class PersonValidator {

    public boolean isValidEmail(String email){
        // Basic email validation using regular expression
        return email.matches(EMAIL_REGEX);
    }

    public void validatePerson(PersonDTO personDTO) throws Exception{
        if(!isValidEmail(personDTO.getEmail())){
            throw new InvalidEmail("Email-ul nu este corect!");
        }

    }
}
