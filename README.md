# ExpiryEye 👁️ (MySQL Edition)
> **Tagline:** Never lose money or safety because of a missed expiry.

ExpiryEye is a full-stack household safety & expiry tracking web application. This version uses **MySQL** database for production-grade data storage.

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot 3.1.5, Spring Data JPA
- **Database:** MySQL 8.0+ (`mysql-connector-j`)
- **Frontend:** Vanilla JavaScript (ES6+), Semantic HTML5, CSS3 (Dark Navy Safety Design)
- **Testing:** JUnit 5

---

## 🗄️ MySQL Database Setup (XAMPP)

1. Open **XAMPP Control Panel** and click **Start** next to **MySQL**.
2. Open `backend/src/main/resources/application.properties` and verify DB credentials:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=
   ```
3. Run the backend; Spring Boot will automatically create the `expiryeye_db` database and insert demo records.

---

## 🚀 How to Run Locally

### 1. Run Backend
```bash
cd backend
mvn clean spring-boot:run
```
*Backend API runs at:* `http://localhost:8080/api`

### 2. Run Frontend
```bash
cd frontend
python -m http.server 5500
```
*Open Browser:* `http://localhost:5500` (or double-click `frontend/index.html`)
