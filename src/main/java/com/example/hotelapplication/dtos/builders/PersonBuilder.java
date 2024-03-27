package com.example.hotelapplication.dtos.builders;

import com.example.hotelapplication.dtos.PersonDTO;
import com.example.hotelapplication.entities.Person;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor
public class PersonBuilder {

    private static final ModelMapper modelMapper = new ModelMapper();
    public static Person stoEntity(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);

    }
    public static PersonDTO etoPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }
}
