package com.bookstore.controllers;

import com.bookstore.models.Book;
import com.bookstore.models.Review;
import com.bookstore.models.User;
import com.bookstore.services.BookService;
import com.bookstore.services.ReviewService;
import com.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String bookDetail(@PathVariable Long id,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "5") int size,
                           Authentication authentication,
                           Model model) {
        
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getReviewsByBook(book, pageable);
        
        Double averageRating = reviewService.getAverageRating(book);
        long reviewCount = reviewService.getReviewCount(book);

        boolean hasUserReviewed = false;
        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName())
                    .orElse(null);
            if (user != null) {
                hasUserReviewed = reviewService.hasUserReviewedBook(user, book);
            }
        }

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("hasUserReviewed", hasUserReviewed);
        model.addAttribute("newReview", new Review());

        return "books/detail";
    }

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id,
                           @RequestParam Integer rating,
                           @RequestParam String comment,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        
        try {
            Book book = bookService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            reviewService.createReview(user, book, rating, comment);
            redirectAttributes.addFlashAttribute("success", "Review added successfully!");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/books/" + id;
    }
} 