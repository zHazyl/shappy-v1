package com.bookstore.repositories;

import com.bookstore.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    Page<Book> findByGenreIgnoreCase(String genre, Pageable pageable);
    
    Page<Book> findByTitleContainingIgnoreCaseAndGenreIgnoreCase(String title, String genre, Pageable pageable);
    
    Page<Book> findByAuthorContainingIgnoreCaseAndGenreIgnoreCase(String author, String genre, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) = LOWER(:genre)) AND " +
           "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> findBooksWithFilters(@Param("title") String title,
                                   @Param("author") String author,
                                   @Param("genre") String genre,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);
    
    @Query("SELECT DISTINCT b.genre FROM Book b ORDER BY b.genre")
    java.util.List<String> findAllGenres();
} 