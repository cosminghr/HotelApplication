package com.example.hotelapplication.dtos.builders;


import com.example.hotelapplication.dtos.ReviewDTO;
import com.example.hotelapplication.entities.Review;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor
public class ReviewBuilder {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Review stoEntity(ReviewDTO reviewDTO) {
        return modelMapper.map(reviewDTO, Review.class);
    }

    public static ReviewDTO etoReviewDTO(Review review) {
        return modelMapper.map(review, ReviewDTO.class);
    }
}
