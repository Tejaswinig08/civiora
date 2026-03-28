# 📌 Civiora - Smart Society Management System

A full-stack society management platform that streamlines maintenance payments, facility bookings, communication, complaint resolution, and financial transparency using secure OTP-based authentication.

---

## ✨ Key Features

🔐 **OTP Authentication** | 💳 **In-App Payments** | 📢 **Smart Notices** | 🏢 **Facility Booking** | 💬 **In-App Chat** | 🛠️ **Complaint System**

- Email-based OTP login (passwordless authentication)
- In-app maintenance payments with transaction history
- Priority-based E-notice board for residents
- Complaint management system with priority tracking and status updates
- Annual financial audit visibility for transparency
- Booking system for:
  - Party halls  
  - Gym  
  - Swimming pool  
  - Sports courts  
  - Clubhouse  
- Real-time in-app chat system for communication

---

## 🛠️ Complaint Management System

- Residents can raise complaints (water, electricity, maintenance, etc.)
- Complaints categorized by **priority**:
  - LOW  
  - MEDIUM  
  - HIGH  
- Track complaint **status**:
  - OPEN  
  - IN_PROGRESS  
  - RESOLVED  
- Admin can manage and resolve complaints efficiently
- Transparent complaint lifecycle tracking

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
