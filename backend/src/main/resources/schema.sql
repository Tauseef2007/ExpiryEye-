-- MySQL Database Schema Initialization
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expiry_date DATE NOT NULL,
    reminder_days INT NOT NULL DEFAULT 7,
    owner_name VARCHAR(255),
    estimated_value DOUBLE,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
