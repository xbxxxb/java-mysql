-- 网上书店管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 部门表
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    parent_department_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_department_id) REFERENCES departments(id)
);

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_date DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    department_id BIGINT,
    manager_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (manager_id) REFERENCES users(id)
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    parent_category_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 供应商表
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100) NOT NULL,
    company_address TEXT,
    tax_id VARCHAR(50),
    business_license VARCHAR(100),
    credit_rating INT DEFAULT 5,
    payment_terms VARCHAR(255),
    minimum_order_amount DECIMAL(10,2),
    discount_rate DECIMAL(5,2),
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    notes TEXT
);

-- 图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    original_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    virtual_stock INT DEFAULT 0,
    description TEXT,
    cover_image VARCHAR(255),
    preview_content TEXT,
    preview_pages INT,
    total_pages INT,
    publication_date DATE,
    publisher VARCHAR(100),
    language VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    sales_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    category_id BIGINT,
    supplier_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- 图书标签关联表
CREATE TABLE IF NOT EXISTS book_tags (
    book_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, tag_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 促销表
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    minimum_amount DECIMAL(10,2),
    maximum_discount DECIMAL(10,2),
    book_id BIGINT,
    category_id BIGINT,
    user_role_id BIGINT,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (user_role_id) REFERENCES roles(id)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS shopping_carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 购物车项目表
CREATE TABLE IF NOT EXISTS shopping_cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shopping_cart_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    final_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    shipping_address TEXT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 订单项目表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 供货记录表
CREATE TABLE IF NOT EXISTS supply_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    supply_date DATETIME NOT NULL,
    expected_delivery_date DATETIME,
    actual_delivery_date DATETIME,
    status VARCHAR(20) NOT NULL,
    invoice_number VARCHAR(50),
    payment_status VARCHAR(20),
    notes TEXT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 用户分类权限表
CREATE TABLE IF NOT EXISTS user_category_permissions (
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, category_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 插入初始数据

-- 插入角色
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', '系统管理员'),
('ROLE_MANAGER', '部门经理'),
('ROLE_USER', '普通用户'),
('ROLE_VIP', 'VIP会员');

-- 插入部门
INSERT INTO departments (name, description) VALUES
('技术部', '负责技术开发和维护'),
('销售部', '负责产品销售'),
('采购部', '负责图书采购'),
('客服部', '负责客户服务');

-- 插入分类
INSERT INTO categories (name, description) VALUES
('计算机技术', '计算机相关技术书籍'),
('文学小说', '文学和小说类书籍'),
('经济管理', '经济和商业管理书籍'),
('教育考试', '教育和考试相关书籍'),
('程序设计', '编程和软件开发书籍'),
('数据库', '数据库相关技术书籍');

-- 插入标签
INSERT INTO tags (name, description) VALUES
('Java', 'Java编程相关'),
('Python', 'Python编程相关'),
('前端', '前端开发相关'),
('后端', '后端开发相关'),
('数据库', '数据库相关'),
('算法', '算法和数据结构'),
('小说', '小说类'),
('管理', '管理类');

-- 插入供应商
INSERT INTO suppliers (name, contact_person, phone_number, email, company_address, credit_rating) VALUES
('人民邮电出版社', '张经理', '010-12345678', 'contact@ptpress.com.cn', '北京市朝阳区', 9),
('机械工业出版社', '李经理', '010-87654321', 'contact@cmpedu.com', '北京市西城区', 8),
('电子工业出版社', '王经理', '010-11223344', 'contact@phei.com.cn', '北京市海淀区', 8);

-- 插入用户（密码为123456的BCrypt加密）
INSERT INTO users (username, password, email, full_name, department_id, is_active) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@bookstore.com', '系统管理员', 1, TRUE),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'manager@bookstore.com', '部门经理', 2, TRUE),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'user1@example.com', '张三', 3, TRUE);

-- 插入用户角色关联
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ROLE_ADMIN
(2, 2), -- manager -> ROLE_MANAGER
(3, 3); -- user1 -> ROLE_USER

-- 插入图书
INSERT INTO books (title, author, isbn, original_price, current_price, stock, description, category_id, supplier_id) VALUES
('Java核心技术', 'Cay S. Horstmann', '9787115471653', 89.00, 89.00, 100, 'Java编程经典教材', 5, 1),
('Python编程：从入门到实践', 'Eric Matthes', '9787115428028', 79.00, 79.00, 80, 'Python入门教程', 5, 1),
('深入理解计算机系统', 'Randal E. Bryant', '9787111321330', 99.00, 99.00, 50, '计算机系统经典教材', 1, 2),
('算法导论', 'Thomas H. Cormen', '9787111187776', 128.00, 128.00, 30, '算法经典教材', 1, 2),
('数据库系统概念', 'Abraham Silberschatz', '9787111526285', 89.00, 89.00, 60, '数据库系统教材', 6, 3);

-- 插入图书标签关联
INSERT INTO book_tags (book_id, tag_id) VALUES
(1, 1), -- Java核心技术 -> Java
(2, 2), -- Python编程 -> Python
(3, 6), -- 深入理解计算机系统 -> 算法
(4, 6), -- 算法导论 -> 算法
(5, 5); -- 数据库系统概念 -> 数据库

-- 插入促销活动
INSERT INTO promotions (name, description, type, discount_value, discount_percentage, start_date, end_date, category_id) VALUES
('计算机图书8折优惠', '计算机技术类图书8折优惠', 'PERCENTAGE_DISCOUNT', 0.00, 20.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
('VIP用户专享9折', 'VIP用户购买图书享受9折优惠', 'USER_TYPE_DISCOUNT', 0.00, 10.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NULL);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_promotions_active ON promotions(is_active, start_date, end_date);