# 📚 Book Images Guide

This guide explains how to add and manage book cover images in the bookstore application.

## 📁 Directory Structure

```
bookstore/
├── images/books/           # Local development images
│   ├── great-gatsby.jpg
│   ├── 1984.jpg
│   └── ... (other book covers)
├── setup-images.sh         # Script to create placeholder images
└── src/main/resources/
    └── application-dev.properties  # Development image config
```

## 🔧 Configuration

### **Local Development**
- **Path**: `./images/books/`
- **Config**: `application-dev.properties`
- **URL Access**: `http://localhost:8080/images/books/filename.jpg`

### **Docker/Production**
- **Path**: `/app/images/books/`
- **Config**: `application.properties`
- **Environment Variable**: `IMAGES_PATH=/app/images`

## 🚀 Quick Setup

### **1. Create Placeholder Images**
```bash
./setup-images.sh
```

This script will:
- Create the `images/books/` directory
- Generate colored placeholder images (if ImageMagick is installed)
- Create empty files as fallback

### **2. Run with Development Profile**
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 📷 Adding New Images

### **Method 1: Manual Addition**

1. **Add image file**:
   ```bash
   # Local development
   cp your-book-cover.jpg images/books/new-book.jpg
   
   # Production (Docker)
   docker cp your-book-cover.jpg bookstore-app:/app/images/books/new-book.jpg
   ```

2. **Update database**:
   ```sql
   UPDATE books 
   SET image_url = '/images/books/new-book.jpg' 
   WHERE id = YOUR_BOOK_ID;
   ```

3. **Or add via Admin Panel**:
   - Login as admin
   - Go to `/admin/books`
   - Add/edit book with image URL: `/images/books/new-book.jpg`

### **Method 2: Via Admin Interface** *(Future Enhancement)*

A file upload feature could be added to the admin panel for easier image management.

## 📋 Current Book Images Needed

Based on the sample data, these images are expected:

| Filename | Book Title |
|----------|------------|
| `great-gatsby.jpg` | The Great Gatsby |
| `to-kill-mockingbird.jpg` | To Kill a Mockingbird |
| `1984.jpg` | 1984 |
| `pride-prejudice.jpg` | Pride and Prejudice |
| `catcher-rye.jpg` | The Catcher in the Rye |
| `lord-flies.jpg` | Lord of the Flies |
| `hobbit.jpg` | The Hobbit |
| `harry-potter-1.jpg` | Harry Potter and the Philosopher's Stone |
| `da-vinci-code.jpg` | The Da Vinci Code |
| `alchemist.jpg` | The Alchemist |
| `brave-new-world.jpg` | Brave New World |
| `lotr-fellowship.jpg` | The Lord of the Rings: Fellowship |
| `dune.jpg` | Dune |
| `hunger-games.jpg` | The Hunger Games |
| `gone-girl.jpg` | Gone Girl |

## 🔍 Troubleshooting

### **Images Not Showing**

1. **Check file exists**:
   ```bash
   ls -la images/books/
   ```

2. **Check URL in database**:
   ```sql
   SELECT title, image_url FROM books;
   ```

3. **Check application logs**:
   ```bash
   # Look for file access errors
   docker logs bookstore-app
   ```

4. **Test direct access**:
   ```bash
   curl -I http://localhost:8080/images/books/great-gatsby.jpg
   ```

### **Permission Issues**

```bash
# Fix permissions in Docker
docker exec bookstore-app chown -R appuser:appgroup /app/images
```

## 💡 Image Recommendations

- **Format**: JPG or PNG
- **Size**: 300x450 pixels (book cover ratio)
- **File Size**: Under 500KB for optimal loading
- **Naming**: Use lowercase, hyphen-separated names (e.g., `great-gatsby.jpg`)

## 🔄 Docker Volume Mapping

For persistent images in Docker, map the images directory:

```yaml
# docker-compose.yml
services:
  app:
    volumes:
      - ./images:/app/images
```

This ensures images persist between container restarts.

## 🛠️ Future Enhancements

Potential improvements for image management:

1. **Admin File Upload**: Direct upload through admin interface
2. **Image Resizing**: Automatic thumbnail generation
3. **CDN Integration**: Store images in cloud storage (AWS S3, etc.)
4. **Image Validation**: Check format and size during upload
5. **Bulk Import**: Script to import multiple images at once 