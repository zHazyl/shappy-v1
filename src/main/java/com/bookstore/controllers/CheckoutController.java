package com.bookstore.controllers;

import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.User;
import com.bookstore.services.CartService;
import com.bookstore.services.OrderService;
import com.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String checkoutPage(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal cartTotal = cartService.getCartTotal(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "checkout/checkout";
    }

    @PostMapping
    public String processCheckout(@RequestParam String shippingAddress,
                                 @RequestParam String deliveryMethod,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.createOrder(user, shippingAddress, deliveryMethod);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/checkout/confirmation/" + order.getId();

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId,
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

        return "checkout/confirmation";
    }
} 