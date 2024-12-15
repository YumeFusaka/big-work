use bigwork;
-- 出版社表
CREATE TABLE Publisher (
    publisher_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    contact VARCHAR(50)
);

-- 图书类别表
CREATE TABLE BookCategory (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 期刊类别表
CREATE TABLE MagazineCategory (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 图书表
CREATE TABLE Book (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    publisher_id INT,
    category_id INT,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(10) CHECK (unit = '本'),
    FOREIGN KEY (publisher_id) REFERENCES Publisher(publisher_id),
    FOREIGN KEY (category_id) REFERENCES BookCategory(category_id)
);

-- 期刊表
CREATE TABLE Magazine (
    magazine_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    publisher_id INT,
    category_id INT,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(10) CHECK (unit = '本'),
    FOREIGN KEY (publisher_id) REFERENCES Publisher(publisher_id),
    FOREIGN KEY (category_id) REFERENCES MagazineCategory(category_id)
);

-- 客户表
CREATE TABLE Customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- 借阅记录表
CREATE TABLE Borrowing (
    borrow_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    item_type VARCHAR(10), -- 'BOOK' or 'MAGAZINE'
    item_id INT,
    borrow_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20), -- 'BORROWED' or 'RETURNED'
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

-- 销售记录表
CREATE TABLE Sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    item_type VARCHAR(10), -- 'BOOK' or 'MAGAZINE'
    item_id INT,
    quantity INT NOT NULL,
    sale_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

-- 借阅时更新可用数量
DELIMITER //
CREATE TRIGGER after_borrowing_insert
AFTER INSERT ON Borrowing
FOR EACH ROW
BEGIN
    IF NEW.item_type = 'BOOK' THEN
        UPDATE Book SET available_quantity = available_quantity - 1
        WHERE book_id = NEW.item_id;
    ELSE
        UPDATE Magazine SET available_quantity = available_quantity - 1
        WHERE magazine_id = NEW.item_id;
    END IF;
END //
DELIMITER ;

-- 归还时更新可用数量
DELIMITER //
CREATE TRIGGER after_borrowing_update
AFTER UPDATE ON Borrowing
FOR EACH ROW
BEGIN
    IF NEW.status = 'RETURNED' AND OLD.status = 'BORROWED' THEN
        IF NEW.item_type = 'BOOK' THEN
            UPDATE Book SET available_quantity = available_quantity + 1
            WHERE book_id = NEW.item_id;
        ELSE
            UPDATE Magazine SET available_quantity = available_quantity + 1
            WHERE magazine_id = NEW.item_id;
        END IF;
    END IF;
END //
DELIMITER ;

-- 销售时更新总数量和可用数量
DELIMITER //
CREATE TRIGGER after_sales_insert
AFTER INSERT ON Sales
FOR EACH ROW
BEGIN
    IF NEW.item_type = 'BOOK' THEN
        UPDATE Book 
        SET total_quantity = total_quantity - NEW.quantity,
            available_quantity = available_quantity - NEW.quantity
        WHERE book_id = NEW.item_id;
    ELSE
        UPDATE Magazine 
        SET total_quantity = total_quantity - NEW.quantity,
            available_quantity = available_quantity - NEW.quantity
        WHERE magazine_id = NEW.item_id;
    END IF;
END //
DELIMITER ;

-- 统计指定时间段内的借阅和销售数量
DELIMITER //
CREATE PROCEDURE sp_statistics_by_period(
    IN start_date DATE,
    IN end_date DATE
)
BEGIN
    -- 图书借阅统计
    SELECT 'BOOK' as item_type, b.title, 
           COUNT(*) as borrow_count,
           0 as sale_count
    FROM Borrowing br
    JOIN Book b ON br.item_id = b.book_id
    WHERE br.item_type = 'BOOK'
    AND br.borrow_date BETWEEN start_date AND end_date
    GROUP BY b.book_id, b.title
    
    UNION ALL
    
    -- 期刊借阅统计
    SELECT 'MAGAZINE' as item_type, m.title,
           COUNT(*) as borrow_count,
           0 as sale_count
    FROM Borrowing br
    JOIN Magazine m ON br.item_id = m.magazine_id
    WHERE br.item_type = 'MAGAZINE'
    AND br.borrow_date BETWEEN start_date AND end_date
    GROUP BY m.magazine_id, m.title
    
    UNION ALL
    
    -- 图书销售统计
    SELECT 'BOOK' as item_type, b.title,
           0 as borrow_count,
           SUM(s.quantity) as sale_count
    FROM Sales s
    JOIN Book b ON s.item_id = b.book_id
    WHERE s.item_type = 'BOOK'
    AND s.sale_date BETWEEN start_date AND end_date
    GROUP BY b.book_id, b.title
    
    UNION ALL
    
    -- 期刊销售统计
    SELECT 'MAGAZINE' as item_type, m.title,
           0 as borrow_count,
           SUM(s.quantity) as sale_count
    FROM Sales s
    JOIN Magazine m ON s.item_id = m.magazine_id
    WHERE s.item_type = 'MAGAZINE'
    AND s.sale_date BETWEEN start_date AND end_date
    GROUP BY m.magazine_id, m.title;
END //
DELIMITER ;

-- 统计指定客户的借阅和购买情况
DELIMITER //
CREATE PROCEDURE sp_statistics_by_customer(
    IN customer_id INT
)
BEGIN
    -- 借阅统计
    SELECT 'BORROW' as operation_type,
           br.item_type,
           COALESCE(b.title, m.title) as title,
           1 as quantity,
           br.borrow_date as operation_date
    FROM Borrowing br
    LEFT JOIN Book b ON br.item_type = 'BOOK' AND br.item_id = b.book_id
    LEFT JOIN Magazine m ON br.item_type = 'MAGAZINE' AND br.item_id = m.magazine_id
    WHERE br.customer_id = customer_id
    
    UNION ALL
    
    -- 购买统计
    SELECT 'SALE' as operation_type,
           s.item_type,
           COALESCE(b.title, m.title) as title,
           s.quantity,
           s.sale_date as operation_date
    FROM Sales s
    LEFT JOIN Book b ON s.item_type = 'BOOK' AND s.item_id = b.book_id
    LEFT JOIN Magazine m ON s.item_type = 'MAGAZINE' AND s.item_id = m.magazine_id
    WHERE s.customer_id = customer_id
    ORDER BY operation_date;
END //
DELIMITER ; 