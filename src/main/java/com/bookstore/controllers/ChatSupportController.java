package com.bookstore.controllers;

import com.bookstore.models.Book;
import com.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/chat")
public class ChatSupportController {

    @Autowired
    private BookService bookService;

    private static final Map<String, String> FAQ = new HashMap<>();
    
    static {
        FAQ.put("shipping", "We offer free shipping on all orders! Standard delivery takes 3-5 business days, express delivery takes 1-2 business days.");
        FAQ.put("return", "You can return any book within 30 days of purchase for a full refund. The book must be in original condition.");
        FAQ.put("payment", "We accept all major credit cards, PayPal, and bank transfers. All payments are processed securely.");
        FAQ.put("order status", "You can check your order status by going to 'My Orders' in your account. You'll also receive email updates.");
        FAQ.put("account", "To create an account, click 'Register' in the top menu. You'll need a valid email address and password.");
        FAQ.put("discount", "Sign up for our newsletter to receive exclusive discounts and book recommendations!");
        FAQ.put("recommendation", "Our system can recommend books based on your reading history and preferences. Browse our catalog to get started!");
        FAQ.put("genre", "We have books in many genres including Fiction, Romance, Fantasy, Science Fiction, Mystery, Biography, and more!");
        FAQ.put("author", "You can search for books by author using our search feature on the home page.");
        FAQ.put("new releases", "Check our home page regularly for new book releases and featured titles!");
    }

    @PostMapping("/message")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleChatMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userMessage == null || userMessage.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Please enter a message.");
                return ResponseEntity.badRequest().body(response);
            }

            String botResponse = generateResponse(userMessage.toLowerCase().trim());
            List<Book> suggestedBooks = findRelevantBooks(userMessage.toLowerCase().trim());

            response.put("success", true);
            response.put("botResponse", botResponse);
            response.put("suggestedBooks", suggestedBooks);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Sorry, I encountered an error. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private String generateResponse(String userMessage) {
        // Check for book search queries
        if (userMessage.contains("book") || userMessage.contains("find") || userMessage.contains("search") || 
            userMessage.contains("recommend") || userMessage.contains("suggest")) {
            return "I can help you find books! I've found some recommendations based on your message. You can also use our search feature on the home page to find specific titles, authors, or genres.";
        }

        // Check FAQ topics
        for (Map.Entry<String, String> entry : FAQ.entrySet()) {
            if (userMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Greeting responses
        if (userMessage.contains("hello") || userMessage.contains("hi") || userMessage.contains("hey")) {
            return "Hello! Welcome to BookHaven! 📚 I'm here to help you with any questions about our books, orders, shipping, or anything else. What can I assist you with today?";
        }

        if (userMessage.contains("thank") || userMessage.contains("thanks")) {
            return "You're very welcome! If you have any other questions, feel free to ask. Happy reading! 📖";
        }

        // Default response with helpful suggestions
        return "I'd be happy to help you! Here are some things I can assist with:\n\n" +
               "📚 Finding books by title, author, or genre\n" +
               "🚚 Shipping information and delivery times\n" +
               "💳 Payment methods and order status\n" +
               "🔄 Returns and refunds\n" +
               "💡 Book recommendations\n\n" +
               "Just ask me about any of these topics!";
    }

    private List<Book> findRelevantBooks(String userMessage) {
        try {
            List<Book> allBooks = bookService.findAll();
            List<Book> relevantBooks = new ArrayList<>();

            // Search for books based on keywords in the message
            Set<String> keywords = extractKeywords(userMessage);
            
            for (Book book : allBooks) {
                int relevanceScore = calculateRelevanceScore(book, keywords, userMessage);
                if (relevanceScore > 0) {
                    relevantBooks.add(book);
                }
            }

            // Sort by relevance and return top 3
            return relevantBooks.stream()
                    .sorted((b1, b2) -> Integer.compare(
                            calculateRelevanceScore(b2, keywords, userMessage),
                            calculateRelevanceScore(b1, keywords, userMessage)
                    ))
                    .limit(3)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Set<String> extractKeywords(String message) {
        Set<String> keywords = new HashSet<>();
        String[] words = message.toLowerCase().split("\\s+");
        
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").trim();
            if (word.length() > 2 && !isStopWord(word)) {
                keywords.add(word);
            }
        }
        
        return keywords;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "and", "for", "are", "but", "not", "you", "all", "can", "had", "her", "was", "one", "our", "out", "day", "get", "has", "him", "his", "how", "its", "may", "new", "now", "old", "see", "two", "who", "boy", "did", "way", "she", "use", "your", "time", "they", "have", "book", "find", "want", "need", "like", "love", "read", "about");
        return stopWords.contains(word.toLowerCase());
    }

    private int calculateRelevanceScore(Book book, Set<String> keywords, String originalMessage) {
        int score = 0;
        String bookText = (book.getTitle() + " " + book.getAuthor() + " " + book.getGenre() + " " + book.getDescription()).toLowerCase();
        
        // Check for exact phrase matches (higher score)
        String[] phrases = originalMessage.split("\\.");
        for (String phrase : phrases) {
            phrase = phrase.trim();
            if (phrase.length() > 3 && bookText.contains(phrase)) {
                score += 10;
            }
        }
        
        // Check for keyword matches
        for (String keyword : keywords) {
            if (bookText.contains(keyword)) {
                if (book.getTitle().toLowerCase().contains(keyword)) {
                    score += 5; // Title matches are more important
                } else if (book.getAuthor().toLowerCase().contains(keyword)) {
                    score += 4; // Author matches
                } else if (book.getGenre().toLowerCase().contains(keyword)) {
                    score += 3; // Genre matches
                } else {
                    score += 1; // Description matches
                }
            }
        }
        
        return score;
    }
} 