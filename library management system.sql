-- =========================================
-- LIBRARY MANAGEMENT SYSTEM
-- =========================================

CREATE DATABASE LibraryDB;
USE LibraryDB;

-- ===========================
-- CREATE TABLES
-- ===========================

CREATE TABLE Authors(
    AuthorID INT PRIMARY KEY,
    AuthorName VARCHAR(50)
);

CREATE TABLE Books(
    BookID INT PRIMARY KEY,
    Title VARCHAR(100),
    AuthorID INT,
    Category VARCHAR(30),
    Price DECIMAL(8,2),
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID)
);

CREATE TABLE Members(
    MemberID INT PRIMARY KEY,
    MemberName VARCHAR(50),
    Phone VARCHAR(15)
);

CREATE TABLE Borrow(
    BorrowID INT PRIMARY KEY,
    MemberID INT,
    BookID INT,
    BorrowDate DATE,
    ReturnDate DATE,
    FOREIGN KEY(MemberID) REFERENCES Members(MemberID),
    FOREIGN KEY(BookID) REFERENCES Books(BookID)
);

-- ===========================
-- INSERT DATA
-- ===========================

INSERT INTO Authors VALUES
(1,'J.K. Rowling'),
(2,'George Orwell'),
(3,'Dan Brown');

INSERT INTO Books VALUES
(101,'Harry Potter',1,'Fantasy',550),
(102,'1984',2,'Fiction',350),
(103,'Animal Farm',2,'Fiction',300),
(104,'The Da Vinci Code',3,'Thriller',600),
(105,'Angels and Demons',3,'Thriller',500);

INSERT INTO Members VALUES
(1,'Alice','9876543210'),
(2,'Bob','9876543211'),
(3,'Charlie','9876543212');

INSERT INTO Borrow VALUES
(1,1,101,'2026-01-10','2026-01-20'),
(2,2,104,'2026-01-15','2026-01-30'),
(3,3,102,'2026-01-18','2026-01-28'),
(4,1,105,'2026-02-01',NULL);

-- ===========================
-- QUERIES
-- ===========================

-- 1. View all books
SELECT * FROM Books;

-- 2. Books costing more than 400
SELECT Title, Price
FROM Books
WHERE Price > 400;

-- 3. Books sorted by price
SELECT Title, Price
FROM Books
ORDER BY Price DESC;

-- 4. Total books in each category
SELECT Category, COUNT(*) AS TotalBooks
FROM Books
GROUP BY Category;

-- 5. Average price by category
SELECT Category, AVG(Price) AS AveragePrice
FROM Books
GROUP BY Category;

-- 6. Books with author names
SELECT
Books.Title,
Authors.AuthorName
FROM Books
JOIN Authors
ON Books.AuthorID = Authors.AuthorID;

-- 7. Members who borrowed books
SELECT
Members.MemberName,
Books.Title,
Borrow.BorrowDate
FROM Borrow
JOIN Members
ON Borrow.MemberID = Members.MemberID
JOIN Books
ON Borrow.BookID = Books.BookID;

-- 8. Number of books borrowed by each member
SELECT
Members.MemberName,
COUNT(*) AS BooksBorrowed
FROM Borrow
JOIN Members
ON Borrow.MemberID = Members.MemberID
GROUP BY Members.MemberName;

-- 9. Most expensive book
SELECT Title, Price
FROM Books
WHERE Price = (
SELECT MAX(Price)
FROM Books
);

-- 10. Books not borrowed
SELECT Title
FROM Books
WHERE BookID NOT IN
(
SELECT BookID
FROM Borrow
);

-- 11. Total value of books
SELECT SUM(Price) AS TotalLibraryValue
FROM Books;

-- 12. Members who currently have books
SELECT
Members.MemberName,
Books.Title
FROM Borrow
JOIN Members
ON Borrow.MemberID = Members.MemberID
JOIN Books
ON Borrow.BookID = Books.BookID
WHERE ReturnDate IS NULL;

-- 13. Highest priced book in each category
SELECT Category,
MAX(Price) AS HighestPrice
FROM Books
GROUP BY Category;

-- 14. Borrow history sorted by date
SELECT
Members.MemberName,
Books.Title,
BorrowDate,
ReturnDate
FROM Borrow
JOIN Members
ON Borrow.MemberID = Members.MemberID
JOIN Books
ON Borrow.BookID = Books.BookID
ORDER BY BorrowDate DESC;

-- 15. Total number of members
SELECT COUNT(*) AS TotalMembers
FROM Members;