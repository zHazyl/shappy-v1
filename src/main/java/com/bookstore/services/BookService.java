package com.bookstore.services;

import com.bookstore.models.Book;
import com.bookstore.repositories.BookRepository;
import com.bookstore.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Page<Book> searchBooks(String title, String author, String genre, 
                                 BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        // Use simple search approach that's more reliable
        if (title != null && !title.trim().isEmpty() && genre != null && !genre.trim().isEmpty()) {
            // Search by title and filter by genre
            return bookRepository.findByTitleContainingIgnoreCaseAndGenreIgnoreCase(title, genre, pageable);
        } else if (author != null && !author.trim().isEmpty() && genre != null && !genre.trim().isEmpty()) {
            // Search by author and filter by genre
            return bookRepository.findByAuthorContainingIgnoreCaseAndGenreIgnoreCase(author, genre, pageable);
        } else if (title != null && !title.trim().isEmpty()) {
            // Search by title only
            return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (author != null && !author.trim().isEmpty()) {
            // Search by author only
            return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
        } else if (genre != null && !genre.trim().isEmpty()) {
            // Filter by genre only
            return bookRepository.findByGenreIgnoreCase(genre, pageable);
        } else {
            // No filters, return all
            return bookRepository.findAll(pageable);
        }
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<String> getAllGenres() {
        return bookRepository.findAllGenres();
    }

    public Double getAverageRating(Book book) {
        Double avg = reviewRepository.getAverageRatingForBook(book);
        return avg != null ? avg : 0.0;
    }

    public long getReviewCount(Book book) {
        return reviewRepository.getReviewCountForBook(book);
    }

    public long getTotalBooks() {
        return bookRepository.count();
    }
} 