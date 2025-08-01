package com.bookstore.services;

import com.bookstore.models.*;
import com.bookstore.repositories.*;
import com.bookstore.enums.OrderStatus;
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
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    public Order createOrder(User user, String shippingAddress, String deliveryMethod) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = cartService.getCartTotal(user);

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .shippingAddress(shippingAddress)
                .deliveryMethod(deliveryMethod)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getBook().getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        cartService.clearCart(user);

        return savedOrder;
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Page<Order> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<OrderItem> getOrderItems(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    public long getTotalOrders() {
        return orderRepository.countTotalOrders();
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenueByStatus(OrderStatus.DELIVERED);
    }
} 