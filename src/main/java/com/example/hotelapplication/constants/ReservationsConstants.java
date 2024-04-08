package com.example.hotelapplication.constants;

public class ReservationsConstants {
    public static final String ROOM_FOUND = "Room with id {} was found in db";

    public static final String ROOM_NOT_FOUND ="Room with id {} was not found in db";

    public static final String PERSON_NOT_FOUND="Person with id {} was not found in db";
    public static final String PERSON_FOUND="Person with id {} was found in db";

    public static final String RESERVATION_NOT_FOUND="Reservation with id {} was not found in db";
    public static final String RESERVATION_FOUND="Reservation with id {} was found in db";
    public static final String LIST_EMPTY="List of rooms is empty. Reservation creation aborted.";
    public static final String ROOM_NOT_AVAILABLE="Room with id {} is not available for the specified period. Reservation creation aborted.";
    public static final String BAD_TIME="Reservation start date is in the past or end date is before start date. Reservation creation aborted.";
    public static final String RESERVATION_INSERT="Reservation with id {} was inserted in db";
    public static final String RESERVATION_UPDATED="Reservation with id {} was updated in db";

    public static final String RESERVATION_DELETED="Reservation with id {} deleted successfully.";

    public static final String RESERVATION_NOT_DELETED="Reservation with id {} not found. Delete operation aborted.";


    public static final String OLD_COST="Old Total Price: {}";
    public static final String NEW_COST="New Total Price After Applying {}% Discount: {}";

}
