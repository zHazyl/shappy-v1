package com.bookstore.services;

import com.bookstore.models.Review;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import com.bookstore.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(User user, Book book, Integer rating, String comment) {
        // Check if user has already reviewed this book
        if (reviewRepository.existsByUserAndBook(user, book)) {
            throw new RuntimeException("You have already reviewed this book");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .comment(comment)
                .build();

        return reviewRepository.save(review);
    }

    public Page<Review> getReviewsByBook(Book book, Pageable pageable) {
        return reviewRepository.findByBookOrderByCreatedAtDesc(book, pageable);
    }

    public Optional<Review> findByUserAndBook(User user, Book book) {
        return reviewRepository.findByUserAndBook(user, book);
    }

    public boolean hasUserReviewedBook(User user, Book book) {
        return reviewRepository.existsByUserAndBook(user, book);
    }

    public Double getAverageRating(Book book) {
        Double avg = reviewRepository.getAverageRatingForBook(book);
        return avg != null ? avg : 0.0;
    }

    public long getReviewCount(Book book) {
        return reviewRepository.getReviewCountForBook(book);
    }
} 