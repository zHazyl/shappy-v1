package com.bookstore.repositories;

import com.bookstore.models.Cart;
import com.bookstore.models.CartItem;
import com.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCart(Cart cart);
    
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);
    
    void deleteByCart(Cart cart);
} 