-- Run this inside MySQL Workbench after selecting your database
USE grocery_shopingDB;

CREATE TABLE IF NOT EXISTS users (
    userID    INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20)  DEFAULT 'user',
    createdAt DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product (
    productID    INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL UNIQUE,
    category     VARCHAR(100)  DEFAULT 'General',
    price        DECIMAL(10,2) NOT NULL,
    quantity     INT           NOT NULL DEFAULT 0,
    minimumStock INT           NOT NULL DEFAULT 5,
    createdAt    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updatedAt    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sales (
    saleID      INT AUTO_INCREMENT PRIMARY KEY,
    totalAmount DECIMAL(10,2) NOT NULL,
    saleDate    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    userID      INT,
    FOREIGN KEY (userID) REFERENCES users(userID)
);

CREATE TABLE IF NOT EXISTS sale_items (
    saleItemID INT AUTO_INCREMENT PRIMARY KEY,
    quantity   INT           NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    saleID     INT           NOT NULL,
    productID  INT           NOT NULL,
    FOREIGN KEY (saleID)    REFERENCES sales(saleID),
    FOREIGN KEY (productID) REFERENCES product(productID)
);

CREATE TABLE IF NOT EXISTS stock_alert (
    alertID      INT AUTO_INCREMENT PRIMARY KEY,
    productID    INT          NOT NULL,
    alertMessage VARCHAR(255),
    alertDate    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    resolved     BOOLEAN      DEFAULT FALSE,
    FOREIGN KEY (productID) REFERENCES product(productID)
);

-- Default admin (change password after first login)
INSERT IGNORE INTO users (username, password, role)
VALUES ('admin', 'Admin@123', 'admin');
