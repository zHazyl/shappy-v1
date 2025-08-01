package com.bookstore.controllers;

import com.bookstore.models.Book;
import com.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @GetMapping("/home")
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "12") int size,
                      @RequestParam(value = "search", required = false) String search,
                      @RequestParam(value = "genre", required = false) String genre,
                      @RequestParam(value = "sort", defaultValue = "title") String sort,
                      Model model) {

        // Handle sort parameter and determine direction
        Sort.Direction direction = Sort.Direction.ASC;
        String sortField = "title";
        
        switch (sort) {
            case "title":
                sortField = "title";
                direction = Sort.Direction.ASC;
                break;
            case "author":
                sortField = "author";
                direction = Sort.Direction.ASC;
                break;
            case "price":
                sortField = "price";
                direction = Sort.Direction.ASC;
                break;
            case "priceDesc":
                sortField = "price";
                direction = Sort.Direction.DESC;
                break;
            default:
                sortField = "title";
                direction = Sort.Direction.ASC;
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        Page<Book> books;
        
        // Use search parameter for both title and author search
        if (search != null && !search.trim().isEmpty() || genre != null && !genre.trim().isEmpty()) {
            books = bookService.searchBooks(
                search != null ? search.trim() : null,  // title
                search != null ? search.trim() : null,  // author (same search term)
                genre != null && !genre.trim().isEmpty() ? genre : null,  // genre
                null,  // minPrice
                null,  // maxPrice
                pageable
            );
        } else {
            books = bookService.getAllBooks(pageable);
        }

        List<String> genres = bookService.getAllGenres();

        model.addAttribute("books", books);
        model.addAttribute("genres", genres);
        model.addAttribute("search", search);
        model.addAttribute("genre", genre);
        model.addAttribute("sort", sort);

        return "home";
    }
} 