# App In Science University Club Member Space Platform

A comprehensive digital platform designed to streamline university club management and enhance member engagement. Built with modern microservices architecture using Spring Boot, React, and containerized deployment.

## Platform Overview

The University Club Member Space Platform is a full-stack web application that provides clubs with tools to manage members, organize events, facilitate communication, and handle administrative tasks. The platform is designed to be scalable, maintainable, and user-friendly for both club administrators and members.

## Tech Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security
- **Build Tool**: Gradle
- **Containerization**: Docker & Docker Compose

### Infrastructure
- **Reverse Proxy**: Caddy (SSL termination & routing)
- **Architecture**: Microservices
- **Deployment**: Docker Compose

## Microservices Architecture

The platform consists of 4 independent microservices, each handling specific business domains:

### 1. Auth Service (Current Service)

**Purpose**: Handles authentication, authorization, and user access control

**Port**: 8081

**Responsibilities**:
- User registration and login
- JWT token generation and validation
- Role-based access control (ADMIN/USER)
- Account activation workflow
- Security management

**Key Features**:
- Secure password encryption with BCrypt
- Admin-controlled account activation
- Stateless JWT authentication
- Role-based endpoint protection

### 2. Chat Service (Planned)

**Purpose**: Manages chat rooms, message storage, and chat-related data persistence

**Port**: 8082 & 8083

**Responsibilities**:
- Chat room creation and management
- Message storage and retrieval
- Chat history and persistence
- User chat permissions
- Message moderation and filtering
- Chat room member management
- Real-time message broadcasting
- WebSocket connection management
- Online user presence tracking
- Live chat functionality
- Message delivery confirmation
- Connection handling and scaling

**Key Features**:
- Real-time bidirectional communication
- Multiple chat room support
- Online/offline user status
- Message delivery receipts
- Scalable WebSocket connections

### 4. Event Service (Planned)

**Purpose**: Handles club events and activities management

**Port**: 8084

**Responsibilities**:
- Event creation and management
- Event registration and attendance
- Calendar integration
- Event notifications
- Activity tracking

### 5. Media Service (Planned)

**Purpose**: Manages all club media content including photos, videos, recordings, and documents

**Port**: 8085

**Responsibilities**:

- Media file upload and storage management
- Photo galleries and album organization
- Video and audio recordings storage
- Document storage and retrieval
- Media metadata and tagging system
- File compression and optimization
- Thumbnail and preview generation
- Media access control and permissions
- Storage quota management
- Media search and filtering capabilities

**Key Features**:
- Multi-format file support (images, videos, audio, documents)
- Automatic thumbnail generation
- Media compression for optimal storage
- Role-based media access control
- Bulk upload functionality
- Media streaming capabilities
- Cloud storage integration options
- Advanced search with metadata tags

**Supported Media Types**:
- **Images**: JPEG, PNG, GIF, WebP (max 10MB)
- **Videos**: MP4, AVI, MOV, WebM (max 500MB)
- **Audio**: MP3, WAV, AAC (max 100MB)
- **Documents**: PDF, DOC, DOCX, PPT (max 50MB)

## Auth Service - Detailed Documentation

### User Roles

- **USER**: Regular club members with standard access
- **ADMIN**: Club administrators with elevated permissions

### API Endpoints

| Method | Endpoint | Description | Auth Required | Role Required |
|--------|----------|-------------|---------------|---------------|
| POST | `/api/v1/auth/signin` | User login | No | None |
| POST | `/api/v1/auth/signup` | User registration | No | None |
| PUT | `/api/v1/auth/activate-account/{id}` | Activate user account | Yes | ADMIN |
| GET | `/api/v1/auth/me` | Get current user info | Yes | Any |
| POST | `/api/v1/auth/health` | Health check | No | None |

### Request/Response Examples

#### Sign In
```http
POST /api/v1/auth/signin
Content-Type: application/json

{
  "email": "user@university.edu",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@university.edu",
    "role": "USER"
  }
}
```

#### Sign Up
```http
POST /api/v1/auth/signup
Content-Type: application/json

{
  "email": "newuser@university.edu",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Activate Account (Admin Only)
```http
PUT /api/v1/auth/activate-account/1
Authorization: Bearer <jwt-token>
```

**Response**:
```
user : John Doe account's has been activated
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL 12+
- Docker & Docker Compose
- Node.js (for frontend development)

### Environment Variables

Create a `.env` file or set the following environment variables:

```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/club_platform
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_minimum_256_bits
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8081
```

### Running the Complete Platform

1. Clone the repository
```bash
git clone <repository-url>
cd university-club-platform
```

2. Start all services with Docker Compose
```bash
docker-compose up -d
```

This will start:
- Auth Service (localhost:8081)
- Chat Service (localhost:8082)
- Chat Server with Netty-SocketIO (localhost:8083)
- Event Service (localhost:8084)
- Frontend (localhost:3000)
- PostgreSQL Database
- Caddy Reverse Proxy (localhost:80/443)

### Running Auth Service Individually

1. Navigate to auth service directory
```bash
cd auth-service
```

2. Install dependencies
```bash
mvn clean install
```

3. Set up PostgreSQL database
```sql
CREATE DATABASE club_platform;
```

4. Run the application
```bash
mvn spring-boot:run
```

## Security Features

- **Password Encryption**: BCrypt hashing algorithm
- **JWT Authentication**: Stateless token-based authentication
- **Role-Based Access Control**: Fine-grained permissions
- **Input Validation**: Comprehensive request validation
- **CORS Configuration**: Controlled cross-origin requests
- **SSL/TLS**: Automatic HTTPS with Caddy

## Monitoring & Health Checks

Each service provides health check endpoints for monitoring:
- Auth Service: `/api/v1/auth/health`
- Load balancer health checks supported
- Docker health check configurations

## Development Workflow

1. **Local Development**: Run individual services with Maven/npm
2. **Integration Testing**: Use Docker Compose for full stack testing
3. **Production Deployment**: Containerized deployment with Caddy

## API Gateway & Routing

Caddy serves as the reverse proxy and handles:
- SSL certificate management (Let's Encrypt)
- Request routing to appropriate microservices
- Load balancing (future scaling)
- Static file serving for frontend

**Routing Rules**:
- `/api/v1/auth/*` → Auth Service (port 8081)
- `/api/v1/chat/*` → Chat Service (port 8082)
- `/socket.io/*` → Chat Server (port 8083)
- `/api/v1/events/*` → Event Service (port 8084)


## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the University Club Member Space Platform.

## Support

For questions and support, please contact the development team or create an issue in the repository.