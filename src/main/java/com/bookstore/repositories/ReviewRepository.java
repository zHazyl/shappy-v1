package com.bookstore.repositories;

import com.bookstore.models.Review;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByBookOrderByCreatedAtDesc(Book book, Pageable pageable);
    
    Optional<Review> findByUserAndBook(User user, Book book);
    
    boolean existsByUserAndBook(User user, Book book);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = :book")
    Double getAverageRatingForBook(@Param("book") Book book);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book = :book")
    long getReviewCountForBook(@Param("book") Book book);
} 