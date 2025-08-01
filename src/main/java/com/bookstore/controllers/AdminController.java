package com.bookstore.controllers;

import com.bookstore.enums.OrderStatus;
import com.bookstore.models.Book;
import com.bookstore.models.Order;
import com.bookstore.services.BookService;
import com.bookstore.services.OrderService;
import com.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookService bookService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminDashboard(Model model) {
        long totalBooks = bookService.getTotalBooks();
        long totalUsers = userService.getTotalUsers();
        long totalOrders = orderService.getTotalOrders();
        BigDecimal totalRevenue = orderService.getTotalRevenue();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String manageBooks(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAllBooks(pageable);

        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("newBook", new Book());

        return "admin/books";
    }

    @PostMapping("/books")
    public String addBook(@Valid @ModelAttribute("newBook") Book book,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the form errors");
            return "redirect:/admin/books";
        }

        try {
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding book: " + e.getMessage());
        }

        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }

        return "redirect:/admin/books";
    }

    @GetMapping("/orders")
    public String manageOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllOrders(pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("orderStatuses", OrderStatus.values());

        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                   @RequestParam OrderStatus status,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }
} 