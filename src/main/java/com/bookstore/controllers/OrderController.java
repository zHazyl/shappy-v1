package com.bookstore.controllers;

import com.bookstore.models.Order;
import com.bookstore.models.User;
import com.bookstore.services.OrderService;
import com.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String orderHistory(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Authentication authentication,
                              Model model) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getOrdersByUser(user, pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "orders/history";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable Long orderId,
                              Authentication authentication,
                              Model model) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify order belongs to user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getOrderItems(order));

        return "orders/detail";
    }
} 