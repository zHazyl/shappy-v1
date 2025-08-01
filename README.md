# Bookstore E-commerce Platform

A comprehensive Spring Boot e-commerce platform for book sales with modern web technologies and responsive design.

## Features

### Core Functionality
- **User Authentication & Authorization**: Spring Security with role-based access (USER, ADMIN)
- **Book Catalog**: Browse, search, and filter books by title, author, genre, and price
- **Shopping Cart**: Add books to cart, update quantities, and manage cart items
- **Order Management**: Complete checkout process with shipping details and order tracking
- **Review System**: Users can rate and review books (1-5 stars with comments)
- **Admin Dashboard**: Admin-only access for managing books, orders, and users

### Technical Features
- **Responsive Design**: Bootstrap-based UI that works on all devices
- **Database Management**: PostgreSQL with Flyway migrations
- **Image Handling**: File-based image storage for book covers (see [IMAGES.md](IMAGES.md))
- **Pagination**: Paginated lists for books, orders, and reviews
- **Search & Filtering**: Advanced search with multiple criteria
- **Security**: Password encryption, CSRF protection, and role-based authorization

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Security**: Spring Security 6
- **Database**: PostgreSQL with Spring Data JPA
- **Migration**: Flyway
- **Frontend**: Thymeleaf with Bootstrap 5
- **Build Tool**: Gradle
- **Containerization**: Docker & Docker Compose

## Quick Start

### Prerequisites
- Java 17 or higher
- Docker and Docker Compose
- Git

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bookstore
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Application: http://localhost:8080
   - Login with demo accounts:
     - **Admin**: username: `admin`, password: `admin123`
     - **Customer**: username: `customer`, password: `cust123`

#### Using PostgreSQL Database

1. **Setup PostgreSQL Database**
   ```bash
   # Using Docker
   docker run --name bookstore-postgres -e POSTGRES_DB=bookstore -e POSTGRES_USER=bookstore_user -e POSTGRES_PASSWORD=bookstore_pass -p 5432:5432 -d postgres:15-alpine
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

## Project Structure

```
bookstore/
├── src/main/java/com/bookstore/
│   ├── BookstoreApplication.java
│   ├── config/                 # Configuration classes
│   ├── controllers/            # Web controllers
│   ├── enums/                  # Enums (Role, OrderStatus)
│   ├── models/                 # JPA entities
│   ├── repositories/           # Data repositories
│   └── services/               # Business logic
├── src/main/resources/
│   ├── db/migration/           # Flyway migration scripts
│   ├── static/                 # Static resources (CSS, JS, images)
│   ├── templates/              # Thymeleaf templates
│   └── application.properties  # Application configuration
├── docker-compose.yml
├── Dockerfile
└── build.gradle
```

## Database Schema

### Core Entities
- **User**: User accounts with authentication
- **Book**: Book catalog with details and pricing
- **Cart/CartItem**: Shopping cart functionality
- **Order/OrderItem**: Order management and history
- **Review**: Book reviews and ratings

### Relationships
- User → Cart (One-to-One)
- User → Orders (One-to-Many)
- User → Reviews (One-to-Many)
- Book → Reviews (One-to-Many)
- Cart → CartItems (One-to-Many)
- Order → OrderItems (One-to-Many)

## API Endpoints

### Public Endpoints
- `GET /` - Redirect to home
- `GET /login` - Login page
- `GET /register` - Registration page
- `POST /register` - User registration

### Authenticated Endpoints
- `GET /home` - Book catalog with search/filter
- `GET /books/{id}` - Book details
- `POST /books/{id}/review` - Add review
- `GET /cart` - View shopping cart
- `POST /cart/add` - Add item to cart
- `POST /cart/update/{itemId}` - Update cart item
- `POST /cart/remove/{itemId}` - Remove cart item
- `GET /checkout` - Checkout page
- `POST /checkout` - Process order
- `GET /orders` - Order history

### Admin Endpoints
- `GET /admin` - Admin dashboard
- `GET /admin/books` - Manage books
- `GET /admin/orders` - Manage orders
- `POST /admin/orders/{id}/status` - Update order status

## Configuration


### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bookstore
SPRING_DATASOURCE_USERNAME=bookstore_user
SPRING_DATASOURCE_PASSWORD=bookstore_pass

# Application Configuration
PORT=8080
IMAGES_PATH=/app/images
```

### Application Properties
Key configurations in `application.properties`:
- Database connection settings
- JPA/Hibernate configuration
- Flyway migration settings
- Thymeleaf template configuration
- File upload settings
- Pagination defaults

## Development

### Running Tests
```bash
./gradlew test
```

### Building the Application
```bash
./gradlew build
```

### Database Migrations
Database schema is managed through Flyway migrations in `src/main/resources/db/migration/`:
- `V1__Create_initial_tables.sql` - Creates all database tables with proper constraints and indexes
- `V2__Insert_initial_data.sql` - Inserts 15 sample books with details

Demo users are created programmatically via `DataLoader.java` component to ensure proper password encryption.

### Adding New Features
1. Create/update entities in `models/` package
2. Add repository interfaces in `repositories/` package
3. Implement business logic in `services/` package
4. Create controllers in `controllers/` package
5. Design Thymeleaf templates in `templates/` directory
6. Add database migrations if needed

## Deployment

### Railway Deployment
The application is configured for Railway deployment:
1. Connect your GitHub repository to Railway
2. Set environment variables for database connection
3. Railway will automatically deploy on push to main branch

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## Sample Data

The application comes with pre-loaded sample data:
- **15 popular books** across various genres (loaded via Flyway migration)
- **2 demo user accounts** created automatically on startup:
  - **Admin**: username: `admin`, password: `admin123` (with ADMIN role)
  - **Customer**: username: `customer`, password: `cust123` (with USER role)
- **Empty carts** automatically created for both users
- All passwords are properly encrypted using BCrypt

## Security Features

- **Password Encryption**: BCrypt password hashing
- **Authentication**: Session-based authentication
- **Authorization**: Role-based access control
- **CSRF Protection**: Configurable CSRF protection
- **SQL Injection Prevention**: JPA parameterized queries
- **Input Validation**: Bean validation on forms

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For issues and questions:
1. Check the existing issues on GitHub
2. Create a new issue with detailed description
3. Include steps to reproduce any bugs 