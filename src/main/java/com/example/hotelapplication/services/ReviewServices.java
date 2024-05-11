package com.example.hotelapplication.services;


import com.example.hotelapplication.dtos.ReviewDTO;
import com.example.hotelapplication.dtos.builders.ReviewBuilder;
import com.example.hotelapplication.entities.Person;
import com.example.hotelapplication.entities.Review;
import com.example.hotelapplication.entities.Rooms;
import com.example.hotelapplication.repositories.PersonRepository;
import com.example.hotelapplication.repositories.ReservationRepository;
import com.example.hotelapplication.repositories.ReviewRepository;
import com.example.hotelapplication.repositories.RoomsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReviewServices {

    private final PersonRepository personRepository;
    private final RoomsRepository roomsRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServices.class);

    public ReviewServices(PersonRepository personRepository, RoomsRepository roomsRepository, ReservationRepository reservationRepository, ReviewRepository reviewRepository) {
        this.personRepository = personRepository;
        this.roomsRepository = roomsRepository;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewDTO> findReviews(){
        List<Review> reviewList = reviewRepository.findAll();
        return reviewList.stream()
                .map(ReviewBuilder::etoReviewDTO)
                .collect(Collectors.toList());
    }

    public UUID insertReview(ReviewDTO reviewDTO){
        Optional<Person> personOptional = personRepository.findById(reviewDTO.getPerson().getId());
        if(personOptional.isEmpty()){
            LOGGER.warn("Person with id {} not found in db.", reviewDTO);
        }

        Optional<Rooms> roomsOptional = roomsRepository.findById(reviewDTO.getRoom().getRoomId());
        if(personOptional.isEmpty()){
            LOGGER.warn("Room with id {} not found in db.", reviewDTO);
        }
        Review review = ReviewBuilder.stoEntity(reviewDTO);
        review = reviewRepository.save(review);

        LOGGER.info("Review with the id {} was inserted", review.getReviewId());
        return review.getReviewId();
    }

    public void deleteReview(UUID id){
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if(optionalReview.isPresent()){
            reviewRepository.deleteById(id);
            LOGGER.info("Review with id {} was deleted", id);
        }else{
            LOGGER.info("Review with id {} was not deleted", id);
        }
    }




}
