# 📌 Civiora - Smart Society Management System

A full-stack society management platform that streamlines maintenance payments, facility bookings, communication, and financial transparency using secure OTP-based authentication.

---

## ✨ Key Features

🔐 **OTP Authentication** | 💳 **In-App Payments** | 📢 **Smart Notices** | 🏢 **Facility Booking** | 💬 **In-App Chat**

- Email-based OTP login (passwordless authentication)
- In-app maintenance payments with transaction history
- Priority-based E-notice board for residents
- Annual financial audit visibility for transparency
- Booking system for:
  - Party halls  
  - Gym  
  - Swimming pool  
  - Sports courts  
  - Clubhouse  
- Real-time in-app chat system for communication

---

## 🏗️ Tech Stack

| Component | Technology |
|----------|-----------|
| **Backend** | Spring Boot, Java |
| **Database** | MySQL |
| **Frontend** | HTML, CSS, JavaScript |
| **Build Tool** | Maven |
| **Port** | 8080 |

---

## 🚀 Quick Start

### Prerequisites
Java 17+ | MySQL | Maven


### Setup
```bash
# 1. Clone project
git clone https://github.com/your-username/civiora.git
cd civiora

# 2. Create database
mysql -u root -p
CREATE DATABASE civiora;

# 3. Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/civiora
spring.datasource.username=root
spring.datasource.password=your_password

# 4. Run project
mvn spring-boot:run

# 5. Open in browser
http://localhost:8080 
```

## 📡 API Architecture
The system follows a modular RESTful API design, organized by domain for scalability and maintainability.

### 🔐 Authentication

| Endpoint | Method | Description |
|----------|--------|------------|
| `/auth/send-otp` | POST | Send OTP to email |
| `/auth/verify-otp` | POST | Verify OTP & login |

### 👤 Users

| Endpoint | Method | Description |
|----------|--------|------------|
| `/users/{id}` | GET | Get user details |
| `/users` | GET | [Admin] Get all users |

### 💳 Payments

| Endpoint | Method | Description |
|----------|--------|------------|
| `/payments` | POST | Pay maintenance |
| `/payments/{userId}` | GET | Get payment history |

### 📢 Notices

| Endpoint | Method | Description |
|----------|--------|------------|
| `/notices` | GET | Get all notices (priority sorted) |
| `/notices` | POST | [Admin] Create notice |

### 🏢 Bookings

| Endpoint | Method | Description |
|----------|--------|------------|
| `/bookings` | POST | Book facility |
| `/bookings/{userId}` | GET | View user bookings |
| `/bookings/{id}` | DELETE | Cancel booking |

### 💬 Chat

| Endpoint | Method | Description |
|----------|--------|------------|
| `/chat` | POST | Send message |
| `/chat/{userId}` | GET | Fetch messages |

### 📊 Audit

| Endpoint | Method | Description |
|----------|--------|------------|
| `/audit` | GET | View annual financial report |

## 📊 Data Models

| Entity | Fields |
|--------|--------|
| **User** | id, name, email, role |
| **Payment** | id, userId, amount, method, date |
| **Notice** | id, title, description, priority, date |
| **Booking** | id, userId, facility, date, timeSlot |
| **Chat** | id, senderId, message, timestamp |
| **Audit** | id, report, year |


## 🔑 Sample API Calls

```bash
# Send OTP
curl -X POST http://localhost:8080/auth/send-otp \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com"}'

# Verify OTP
curl -X POST http://localhost:8080/auth/verify-otp \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","otp":"123456"}'

# Make Payment
curl -X POST http://localhost:8080/payments \
-H "Content-Type: application/json" \
-d '{"userId":1,"amount":2000,"method":"UPI"}'
```

## 📊 Data Models

| Entity | Fields |
|--------|--------|
| **User** | id, name, email, role |
| **Payment** | id, userId, amount, method, date |
| **Notice** | id, title, description, priority, date |
| **Booking** | id, userId, facility, date, timeSlot |
| **Chat** | id, senderId, message, timestamp |
| **Audit** | id, report, year |

## 📂 Project Structure
```bash
civiora/
├── src/main/java/com/civiora/
│ ├── controller/
│ ├── models/
│ ├── dto/
│ ├── repository/
│ └── service/
├── src/main/resources/
│ ├── application.properties
│ └── static/
├── pom.xml
└── README.md
```

## 🔒 Security

- OTP-based authentication (no password storage)  
- Role-based access (admin / resident)  
- Secure payment handling  
- Input validation  

---

## 🎯 Problem Statement

Residential society management often suffers from fragmented communication, manual payment verification, and lack of financial transparency. These inefficiencies lead to missed confirmations, delays, and administrative overhead.

Civiora addresses these challenges by providing a centralized digital platform for payments, facility management, communication, and audit visibility—ensuring efficiency, transparency, and convenience for both residents and administrators.

---

## 🎯 Workflows

**Resident:**  
Login → Pay Maintenance → Book Facilities → View Notices → Chat  
**Admin:**  
Login → Post Notices → View Payments → Manage Bookings → Publish Audit Reports  

---

## 🚀 Future Enhancements

- UPI payment gateway integration  
- Real-time notifications (WebSockets)
- Mobile App Version
