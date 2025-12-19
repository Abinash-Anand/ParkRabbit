# 🚗 Parking Management System (Event-Driven Architecture)

A production-style **event-driven parking management system** built using **Spring Boot**, **React**, **RabbitMQ**, **PostgreSQL**, and **Docker**.

This project demonstrates how backend services communicate **asynchronously** using a message broker to generate and manage notifications without direct frontend involvement.

---

## 📌 Project Goal

The primary goal of this project is to demonstrate:

> **An event-driven notification management system where backend services communicate via RabbitMQ.**

The system focuses on **backend-driven workflows**, scheduled jobs, and message-based communication, while keeping the frontend lightweight and simple.

---

## 🧠 Core Concepts Demonstrated

- Event-Driven Architecture
- Publish–Subscribe pattern (RabbitMQ)
- Backend-triggered notifications
- Decoupled services
- Scheduler / cron-based events
- Containerized deployment (Docker)

---

## 🏗️ System Architecture (High Level)


---

## 🔧 Technology Stack

### Backend
- Java 17
- Spring Boot
- RabbitMQ
- PostgreSQL
- Spring Scheduler

### Frontend
- React (Vite)
- React Router
- Simple in-app notifications (toasts + list)

### Infrastructure
- Docker
- Docker Compose
- GitLab (mono-repo)

---

## ✂️ Scope 



### ✅ Included
- Single parking lot
- Automatic parking slot assignment
- Reservation creation
- Reservation expiry via scheduler
- Event publishing to RabbitMQ
- Notification service consuming events
- In-app notification display
- Fully containerized setup

---

## 🔄 Application Workflow

### 1. Reservation Creation
- User clicks **“Reserve Parking”**
- Parking Service:
  - Creates reservation
  - Publishes `ReservationCreated` event
- Notification Service:
  - Consumes event
  - Stores notification

### 2. Reservation Expiry (Backend-Triggered)
- Scheduler runs periodically
- Expired reservations detected
- Parking Service publishes `ReservationExpired` event
- Notification Service stores expiry notification

### 3. Notification Display
- Frontend polls Notification Service
- New notifications shown as:
  - Toast alerts
  - Notification list

Frontend never triggers notification logic.

---

## 📦 Repository Structure (Mono-Repo)
ParkRabbit/
├── backend/                         # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/parkrabbit/
│   │   │   │   ├── controller/      # REST controllers (API layer)
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # JPA repositories (DB access)
│   │   │   │   ├── model/            # JPA entities / domain models
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   └── ParkRabbitApp.java
│   │   │   └── resources/
│   │   │       ├── application.yml  # App configuration
│   │   │       ├── application-dev.yml
│   │   │       └── application-prod.yml
│   │   └── test/                    # Unit & integration tests
│   ├── pom.xml                      # Maven dependencies
│   ├── Dockerfile                   # Backend container config
│   └── README.md                    # Backend-specific docs
│
├── frontend/                        # React frontend
│   ├── src/
│   │   ├── components/              # Reusable UI components
│   │   ├── pages/                   # Page-level components
│   │   ├── hooks/                   # Custom React hooks
│   │   ├── services/                # API calls
│   │   ├── styles/                  # Global & component styles
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── public/
│   ├── package.json
│   ├── vite.config.js
│   ├── Dockerfile                   # Frontend container config
│   └── README.md                    # Frontend-specific docs
│
├── .env                             # Environment variables (NOT committed)
├── .gitignore
├── docker-compose.yml               # Local dev orchestration
└── README.md                        # Project overview


---

## 🐳 Running the Project (Docker)

### Prerequisites
- Docker
- Docker Compose

### Start the system
```bash
docker-compose up --build


