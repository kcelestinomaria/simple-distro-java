# Distributed Student Management System

## Project Overview
This is a distributed Java application implementing a Student Management System, developed as a semester project for the Advanced Object-Oriented Programming unit at Strathmore University. The system demonstrates key concepts in distributed computing, remote method invocation (RMI), and modern Java application development.

## Architecture
The system is divided into two main components:

1. **ServerProgram**: Handles data storage and business logic
2. **ClientProgram**: Provides the user interface and communicates with the server

### Key Technologies Used
- Java RMI (Remote Method Invocation)
- JavaFX for GUI
- JDBC for database operations
- PDF generation capabilities
- Multi-threading support

## Features

### Authentication System
- User registration and login functionality
- BCrypt password hashing for secure password storage
- Login attempt tracking
- Session management
- Automatic logout functionality

### Student Management
- Create, Read, Update, and Delete (CRUD) operations for student records
- Student information includes:
  - Student ID
  - Name
  - Course
- Search functionality by student ID
- View all students functionality
- PDF report generation

### User Interface
- Modern JavaFX-based GUI
- Tab-based navigation
- Responsive design
- Error handling and user feedback
- Loading indicators for async operations
- Custom styling and theming
- Header with logo/image
- Multi-threaded operations to prevent UI freezing

### Security Features
- BCrypt password hashing
- Login attempt tracking
- Session management
- Input validation
- Error handling and user feedback

## Technical Implementation

### Server-Side Components
- `StudentDBInterface`: Defines the remote interface for client-server communication
- `StudentDBImpl`: Implements the remote interface and handles database operations
- `DBOperations`: Manages database connections and operations
- RMI Registry running on port 1099

### Client-Side Components
- `GUIOne`: Main JavaFX application class
- `StudentDBInterface`: Client-side interface for RMI communication
- PDF generation utilities
- Error handling and user feedback mechanisms

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- NetBeans IDE (recommended)
- MySQL database server

### Setup Instructions
1. Start the MySQL database server
2. Run the ServerProgram first:
   ```bash
   cd ServerProgram
   ant run
   ```
3. Run the ClientProgram:
   ```bash
   cd ClientProgram
   ant run
   ```

## Project Structure

### ServerProgram
```
ServerProgram/
├── src/
│   ├── rmi/           # RMI interface and implementation
│   └── server/        # Server-side database operations
├── build/
├── test/
└── build.xml
```

### ClientProgram
```
ClientProgram/
├── src/
│   ├── rmi/           # RMI interface
│   ├── gui/           # JavaFX GUI components
│   └── client/        # Client-side logic
├── build/
├── test/
└── build.xml
```

## Key Concepts Demonstrated
1. **Distributed Computing**: Using Java RMI for remote method invocation
2. **Object-Oriented Design**: Clean separation of concerns and interface-based programming
3. **Database Management**: JDBC operations and data persistence
4. **User Interface Design**: Modern JavaFX implementation
5. **Error Handling**: Comprehensive exception handling and user feedback
6. **Security**: 
   - BCrypt password hashing for secure password storage
   - Basic authentication and authorization

## Future Improvements
1. **Enhanced Security**:
   - Add role-based access control
   - Implement SSL/TLS for secure communication
   - Add rate limiting for login attempts
   - Implement session timeout

2. **Advanced Features**:
   - Add student attendance tracking
   - Implement grade management
   - Add course management
   - Support for file attachments (e.g., student documents)
   - Add bulk import/export functionality
   - Implement real-time updates

3. **Performance Optimizations**:
   - Implement connection pooling
   - Add caching mechanisms
   - Optimize database queries

4. **User Experience**:
   - Add advanced search filters (by name, course, etc.)
   - Add sorting capabilities
   - Add export to multiple formats (CSV, Excel)
   - Implement real-time updates

5. **Testing and Quality**:
   - Add comprehensive unit tests
   - Implement integration tests
   - Add performance testing
   - Implement continuous integration

## Contributing
This project is open for educational purposes. Feel free to fork and contribute improvements.

## License
This project is part of the Advanced Object-Oriented Programming course at Strathmore University.
