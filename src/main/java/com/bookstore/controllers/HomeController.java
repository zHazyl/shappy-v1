package com.bookstore.controllers;

import com.bookstore.models.Book;
import com.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.pagination.default-size}")
    private int defaultPageSize;

    @GetMapping("/home")
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "12") int size,
                      @RequestParam(value = "sort", defaultValue = "title") String sort,
                      @RequestParam(value = "direction", defaultValue = "asc") String direction,
                      @RequestParam(value = "title", required = false) String title,
                      @RequestParam(value = "author", required = false) String author,
                      @RequestParam(value = "genre", required = false) String genre,
                      @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                      @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                      Model model) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                                     Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Book> books;
        
        if (title != null || author != null || genre != null || minPrice != null || maxPrice != null) {
            books = bookService.searchBooks(title, author, genre, minPrice, maxPrice, pageable);
        } else {
            books = bookService.getAllBooks(pageable);
        }

        List<String> genres = bookService.getAllGenres();

        model.addAttribute("books", books);
        model.addAttribute("genres", genres);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("genre", genre);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "home";
    }
} 