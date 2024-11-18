CREATE DATABASE libreria;
USE libreria;

CREATE TABLE libros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200),
    autor VARCHAR(100),
    categoria VARCHAR(50),
    precio DECIMAL(10,2),
    isbn VARCHAR(13)
);

INSERT INTO libros (titulo, autor, categoria, precio, isbn) VALUES
('Java Programming for Beginners', 'John Smith', 'Programming', 29.99, '9781234567890'),
('Advanced Java Development', 'María García', 'Programming', 49.99, '9789876543210'),
('Python Basics', 'David Brown', 'Programming', 24.99, '9787654321098'),
('Java Enterprise Applications', 'Ana López', 'Programming', 59.99, '9786543210987'),
('Clean Code in Java', 'Robert Martin', 'Programming', 39.99, '9785432109876'),
('JavaScript: The Good Parts', 'Douglas Crockford', 'Programming', 34.99, '9784321098765'),
('Learning Python the Hard Way', 'Zed Shaw', 'Programming', 44.99, '9783210987654');