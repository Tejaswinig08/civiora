# 📌 Civiora - Smart Society Management System

A full-stack society management platform that streamlines maintenance payments, facility bookings, communication, and financial transparency using secure OTP-based authentication.

---

## ✨ Key Features

🔐 **OTP Authentication** | 💳 **In-App Payments** | 📢 **Smart Notices** | 🏢 **Facility Booking** | 💬 **In-App Chat** | 🛠️ **Complaint Management System** (Priority-based) | 👤 **Update Profile Feature**

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
- Residents can raise complaints (e.g., water, electricity, maintenance)
- Complaints categorized by priority (Low, Medium, High)
- Admin can track, update status, and resolve issues
- Transparent complaint lifecycle (Open → Approve → Resolved)
- Update profile allows users to modify their **name, email, and password**, ensuring better communication, flexibility, and account security.

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
CREATE DATABASE spring888;

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
| `/send-otp` | POST | Send OTP to registered email |
| `/verify-otp` | POST | Verify OTP & login |

### 👤 Users

| Endpoint | Method | Description |
|----------|--------|------------|
| `/register` | POST | Register a new user |
| `/user/{id}` | GET | Get user details by ID |
| `/update` | POST | Update user profile (name / email) |

### 💳 Payments (In-App Wallet)

| Endpoint | Method | Description |
|----------|--------|------------|
| `/deposit` | POST | Deposit amount into resident wallet |
| `/passbook/{userId}` | GET | Get transaction history for user |

### 📢 Notices

| Endpoint | Method | Description |
|----------|--------|------------|
| `/admin/notices` | GET | Get all notices (date sorted) |
| `/admin/notices` | POST | [Admin] Create notice |
| `/admin/notices/{id}` | DELETE | [Admin] Delete notice |

### 🏢 Bookings

| Endpoint | Method | Description |
|----------|--------|------------|
| `/bookings` | POST | Book a facility |
| `/bookings/user/{userId}` | GET | View user bookings |
| `/bookings/availability` | GET | Check slot availability (capacity-based) |
| `/bookings/conflict-check` | GET | Check time-range conflict |
| `/admin/bookings` | GET | [Admin] Get all bookings |
| `/admin/bookings/{id}/cancel` | PATCH | [Admin] Cancel a booking |
| `/admin/bookings/{id}/confirm` | PATCH | [Admin] Confirm a booking |

### 💬 Chat

| Endpoint | Method | Description |
|----------|--------|------------|
| `/chat/send` | POST | Send a message (public or private DM) |
| `/chat/messages` | GET | Fetch messages (`?mode=unified&userId=` / `?mode=dm&userId=&with=`) |
| `/chat/messages/since/{lastId}` | GET | Fetch new messages since lastId (polling) |
| `/chat/users` | GET | Get user list for DM recipient dropdown |

### 🛠️ Complaints

| Endpoint | Method | Description |
|----------|--------|------------|
| `/complaints` | POST | Raise a complaint |
| `/complaints/user/{userId}` | GET | View own complaints |
| `/admin/complaints` | GET | [Admin] Get all complaints |
| `/admin/complaints/{id}/status` | PATCH | [Admin] Update complaint status (APPROVED / RESOLVED) |

### 👑 Admin — Users & Maintenance

| Endpoint | Method | Description |
|----------|--------|------------|
| `/admin/users` | GET | [Admin] Get all users |
| `/admin/users` | POST | [Admin] Add resident / admin |
| `/admin/users/{id}` | DELETE | [Admin] Delete user |
| `/admin/payments` | GET | [Admin] Get all maintenance records |
| `/admin/payments` | POST | [Admin] Add maintenance record |
| `/admin/payments/{id}/mark-paid` | PATCH | [Admin] Mark maintenance as paid |
| `/admin/payments/{id}` | DELETE | [Admin] Delete maintenance record |
| `/admin/send-reminders` | POST | [Admin] Email payment reminders to due residents |
| `/admin/transactions` | GET | [Admin] View all wallet transactions (audit log) |
| `/admin/logs` | GET | [Admin] View activity logs |
| `/admin/logs` | DELETE | [Admin] Clear activity logs |

## 🔑 Sample API Calls

```bash
# Send OTP
curl -X POST http://localhost:8080/send-otp \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com"}'

# Verify OTP
curl -X POST http://localhost:8080/verify-otp \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","otp":"123456"}'

# Deposit to wallet
curl -X POST http://localhost:8080/deposit \
-H "Content-Type: application/json" \
-d '{"userId":1,"amount":2000,"description":"Monthly maintenance"}'

# Book a facility
curl -X POST http://localhost:8080/bookings \
-H "Content-Type: application/json" \
-d '{"userId":1,"facility":"gym","date":"2026-04-10","time":"09:00 AM","startTime":"09:00","endTime":"10:00"}'

# Raise a Complaint
curl -X POST http://localhost:8080/complaints \
-H "Content-Type: application/json" \
-d '{"userId":1,"senderName":"Ravi Kumar","subject":"Water Leakage","message":"Leak in bathroom pipe","priority":"HIGH"}'

# Send a public broadcast message
curl -X POST http://localhost:8080/chat/send \
-H "Content-Type: application/json" \
-d '{"userId":1,"message":"Society meeting at 7 PM today"}'

# Send a private DM
curl -X POST http://localhost:8080/chat/send \
-H "Content-Type: application/json" \
-d '{"userId":1,"message":"Can you help me?","receiverId":3}'
```

## 📊 Data Models

| Entity | Fields |
|--------|--------|
| **User** | id, name, email, password, wing, flat, role, balance |
| **Transaction** | id, userId, amount, currBalance, description, date |
| **Maintenance Payment** | id, name, email, wing, flat, amount, month, status, paidOn |
| **Notice** | id, title, category, priority, summary, detail, author, date |
| **Booking** | id, userId, facilityName, bookingDate, bookingTime, startTime, endTime, amount, status, createdAt |
| **ChatMessage** | id, userId, senderName, role, message, receiverId (null=public), sentAt |
| **Complaint** | id, userId, senderName, subject, message, priority, status, createdAt |
| **ActivityLog** | id, action, performedBy, createdAt |

## 📂 Project Structure
```
civiora/
├── src/
│   └── main/
│       ├── java/com/civiora/civiora/
│       │   ├── CivioraApplication.java          ← Spring Boot entry point
│       │   ├── controller/
│       │   │   ├── AccountsController.java
│       │   │   ├── AdminController.java
│       │   │   ├── BookingController.java
│       │   │   ├── ChatController.java
│       │   │   ├── ComplaintController.java
│       │   │   ├── OtpController.java
│       │   │   ├── PaymentController.java
│       │   │   └── UserController.java
│       │   ├── models/
│       │   │   ├── ActivityLog.java
│       │   │   ├── Booking.java
│       │   │   ├── ChatMessage.java
│       │   │   ├── Complaint.java
│       │   │   ├── Notice.java
│       │   │   ├── Payment.java
│       │   │   ├── Transaction.java
│       │   │   └── User.java
│       │   ├── dto/
│       │   │   ├── LoginDto.java
│       │   │   └── UpdateDto.java
│       │   ├── repositories/
│       │   │   ├── ActivityLogRepo.java
│       │   │   ├── BookingRepo.java
│       │   │   ├── ChatRepo.java
│       │   │   ├── ComplaintRepo.java
│       │   │   ├── NoticeRepo.java
│       │   │   ├── PaymentRepo.java
│       │   │   ├── TransactionRepo.java
│       │   │   └── UserRepo.java
│       │   └── service/
│       │       ├── EmailService.java
│       │       └── OtpService.java
│       └── resources/
│           ├── application.properties
│           └── static/
│               ├── index.html                   ← Landing / login redirect
│               ├── login.html
│               ├── signup.html
│               ├── logopage.html
│               ├── home.html
│               ├── dashboard.html
│               ├── admin.html
│               ├── accounts.html
│               ├── booking.html
│               ├── booking-detail.html
│               ├── chat.html
│               ├── complaint.html
│               ├── contact.html
│               ├── history.html
│               ├── notices.html
│               ├── payment.html
│               ├── profile.html
│               ├── gym.png
│               ├── party_hall.png
│               ├── sports_court.png
│               ├── swimming_pool.png
│               ├── games_club.png
│               └── logo.jpeg
├── pom.xml
├── mvnw / mvnw.cmd                              ← Maven wrapper scripts
├── .gitignore
├── HELP.md
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
## 🔥 Recent Enhancements
---
## 📋 Complaints
**Complaint Status:**
- APPROVED  
- RESOLVED  
**Priority Levels:**
- LOW  
- MEDIUM  
- HIGH

## 📩 Contact Us / Feedback System
  - Added a Contact Us section where users can submit feedback.
  - The feedback is sent directly to the developer via email for better communication and improvements.

## ⏱️ Improved Booking System
  - Enhanced booking logic to prevent time slot clashes.
  - If a facility is booked for a specific time (e.g., 12 PM – 5 PM), other users can only book strictly before or after that slot.
  - Ensures smooth and conflict-free facility usage.

## 💬 Advanced Chat System
  - Supports both **peer-to-peer (private chats)** and **public group chats**
  - Enables direct communication between residents as well as community-wide discussions
  - Real-time messaging for better interaction and engagement
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
