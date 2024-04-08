package com.example.hotelapplication.constants;

public class PersonConstants {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String PERSON_NOT_FOUND = "Person with email {} was not found in db";

    public static final String PERSON_FOUND ="Person with email {} was found in db";

    public static final String PERSON_IN="Person with email {} was found in db";
    public static final String PERSON_NOT_IN="Person with email {} was not found in db";

    public static final String EMAIL_NOT_FOUND="Email address {} is already in use by another person. Insertion aborted.";
    public static final String PERSON_INSERT="Person with id {} was inserted in db";

    public static final String PERSON_UPDATED="Person with id {} was updated in db";
    public static final String PERSON_NOT_UPDATED="Person with id {} not found. Update operation aborted.";
    public static final String PERSON_DELETE="Person with id {} and associated reservations deleted successfully.";
    public static final String PERSON_NOT_DELETE="Person with id {} not found. Delete operation aborted.";






}
