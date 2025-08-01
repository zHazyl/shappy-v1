// Main JavaScript file for BookHaven

// Get CSRF token for AJAX requests
function getCSRFToken() {
    const tokenElement = document.querySelector('meta[name="_csrf"]');
    return tokenElement ? tokenElement.getAttribute('content') : null;
}

function getCSRFHeader() {
    const headerElement = document.querySelector('meta[name="_csrf_header"]');
    return headerElement ? headerElement.getAttribute('content') : 'X-CSRF-TOKEN';
}

// Chat Support System
class ChatSupport {
    constructor() {
        this.chatWidget = document.getElementById('chatWidget');
        this.chatToggle = document.getElementById('chatToggle');
        this.chatClose = document.getElementById('chatClose');
        this.chatMessages = document.getElementById('chatMessages');
        this.chatInput = document.getElementById('chatInput');
        this.sendButton = document.getElementById('sendMessage');
        
        this.isOpen = false;
        this.initEventListeners();
        
        // Add welcome message
        this.addMessage('bot', 'Hello! I\'m here to help you find the perfect book. You can ask me about genres, authors, or specific titles!');
    }

    initEventListeners() {
        if (this.chatToggle) {
            this.chatToggle.addEventListener('click', () => this.toggleChat());
        }
        
        if (this.chatClose) {
            this.chatClose.addEventListener('click', () => this.closeChat());
        }
        
        if (this.sendButton) {
            this.sendButton.addEventListener('click', () => this.sendMessage());
        }
        
        if (this.chatInput) {
            this.chatInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.sendMessage();
                }
            });
        }
    }

    toggleChat() {
        if (this.isOpen) {
            this.closeChat();
        } else {
            this.openChat();
        }
    }

    openChat() {
        if (this.chatWidget) {
            this.chatWidget.style.display = 'flex';
            this.isOpen = true;
            if (this.chatInput) {
                this.chatInput.focus();
            }
        }
    }

    closeChat() {
        if (this.chatWidget) {
            this.chatWidget.style.display = 'none';
            this.isOpen = false;
        }
    }

    async sendMessage() {
        const message = this.chatInput?.value?.trim();
        if (!message) return;

        // Add user message
        this.addMessage('user', message);
        this.chatInput.value = '';

        // Add typing indicator
        const typingId = this.addTypingIndicator();

        try {
            const response = await fetch('/api/chat/message', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [getCSRFHeader()]: getCSRFToken()
                },
                body: JSON.stringify({ message: message })
            });

            const data = await response.json();
            
            // Remove typing indicator
            this.removeTypingIndicator(typingId);
            
            if (data.success) {
                this.addMessage('bot', data.response);
                
                // Show recommended books if any
                if (data.books && data.books.length > 0) {
                    this.addBookRecommendations(data.books);
                }
            } else {
                this.addMessage('bot', 'I\'m sorry, I encountered an error. Please try asking in a different way.');
            }
        } catch (error) {
            console.error('Chat error:', error);
            this.removeTypingIndicator(typingId);
            this.addMessage('bot', 'I\'m sorry, I\'m having trouble connecting right now. Please try again later.');
        }
    }

    addMessage(sender, text) {
        if (!this.chatMessages) return;

        const messageDiv = document.createElement('div');
        messageDiv.className = `chat-message ${sender}-message`;
        
        messageDiv.innerHTML = `
            <div class="message-content">
                <p class="mb-1">${this.escapeHtml(text)}</p>
                <small class="text-muted">${this.getCurrentTime()}</small>
            </div>
        `;
        
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }

    addBookRecommendations(books) {
        if (!this.chatMessages || !books.length) return;

        const recommendationsDiv = document.createElement('div');
        recommendationsDiv.className = 'chat-message bot-message book-recommendations';
        
        let booksHtml = '<div class="message-content"><p class="mb-2">Here are some books you might like:</p>';
        
        books.forEach(book => {
            booksHtml += `
                <div class="recommended-book">
                    <div class="d-flex">
                        <img src="${this.escapeHtml(book.imageUrl)}" alt="${this.escapeHtml(book.title)}" class="book-thumbnail">
                        <div class="book-info">
                            <h6 class="mb-1">${this.escapeHtml(book.title)}</h6>
                            <p class="mb-1 text-muted small">by ${this.escapeHtml(book.author)}</p>
                            <p class="mb-1 text-muted small">${this.escapeHtml(book.genre)}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold text-primary">$${book.price}</span>
                                <a href="/books/${book.id}" class="btn btn-sm btn-outline-primary">View Details</a>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });
        
        booksHtml += `<small class="text-muted">${this.getCurrentTime()}</small></div>`;
        recommendationsDiv.innerHTML = booksHtml;
        
        this.chatMessages.appendChild(recommendationsDiv);
        this.scrollToBottom();
    }

    addTypingIndicator() {
        const typingDiv = document.createElement('div');
        const typingId = 'typing-' + Date.now();
        typingDiv.id = typingId;
        typingDiv.className = 'chat-message bot-message typing-indicator';
        
        typingDiv.innerHTML = `
            <div class="message-content">
                <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        `;
        
        this.chatMessages.appendChild(typingDiv);
        this.scrollToBottom();
        
        return typingId;
    }

    removeTypingIndicator(typingId) {
        const typingElement = document.getElementById(typingId);
        if (typingElement) {
            typingElement.remove();
        }
    }

    scrollToBottom() {
        if (this.chatMessages) {
            this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
        }
    }

    getCurrentTime() {
        return new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Image Upload Preview
function setupImageUpload() {
    const imageInput = document.getElementById('imageFile');
    const previewContainer = document.getElementById('imagePreview');
    const previewImage = document.getElementById('previewImage');
    const fileName = document.getElementById('fileName');

    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    if (previewImage) {
                        previewImage.src = e.target.result;
                    }
                    if (previewContainer) {
                        previewContainer.style.display = 'block';
                    }
                    if (fileName) {
                        fileName.textContent = file.name;
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('BookHaven JavaScript loaded');
    
    // Initialize chat support
    if (document.getElementById('chatWidget')) {
        new ChatSupport();
    }
    
    // Setup image upload preview
    setupImageUpload();
    
    // Add animation delays to cards
    const cards = document.querySelectorAll('.fade-in-up');
    cards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
    });
}); 