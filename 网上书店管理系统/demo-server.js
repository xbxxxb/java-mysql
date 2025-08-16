const express = require("express");
const cors = require("cors");
const app = express();
const port = 8080;

// 启用CORS
app.use(cors());
app.use(express.json());

// 模拟数据
const users = [
  {
    id: 1,
    username: "admin",
    email: "admin@bookstore.com",
    fullName: "系统管理员",
    department: "技术部",
    isActive: true,
  },
  {
    id: 2,
    username: "manager",
    email: "manager@bookstore.com",
    fullName: "部门经理",
    department: "销售部",
    isActive: true,
  },
  {
    id: 3,
    username: "user1",
    email: "user1@example.com",
    fullName: "张三",
    department: "采购部",
    isActive: true,
  },
];

const books = [
  {
    id: 1,
    title: "Java核心技术",
    author: "Cay S. Horstmann",
    isbn: "9787115471653",
    price: 89.0,
    stock: 100,
    category: "程序设计",
  },
  {
    id: 2,
    title: "Python编程：从入门到实践",
    author: "Eric Matthes",
    isbn: "9787115428028",
    price: 79.0,
    stock: 80,
    category: "程序设计",
  },
  {
    id: 3,
    title: "深入理解计算机系统",
    author: "Randal E. Bryant",
    isbn: "9787111321330",
    price: 99.0,
    stock: 50,
    category: "计算机技术",
  },
  {
    id: 4,
    title: "算法导论",
    author: "Thomas H. Cormen",
    isbn: "9787111187776",
    price: 128.0,
    stock: 30,
    category: "计算机技术",
  },
  {
    id: 5,
    title: "数据库系统概念",
    author: "Abraham Silberschatz",
    isbn: "9787111526285",
    price: 89.0,
    stock: 60,
    category: "数据库",
  },
];

const orders = [
  {
    id: 1,
    userId: 3,
    orderDate: "2024-01-15",
    totalAmount: 168.0,
    status: "已完成",
    items: 2,
  },
  {
    id: 2,
    userId: 2,
    orderDate: "2024-01-16",
    totalAmount: 99.0,
    status: "处理中",
    items: 1,
  },
];

const suppliers = [
  {
    id: 1,
    name: "人民邮电出版社",
    contactPerson: "张经理",
    phone: "010-12345678",
    email: "contact@ptpress.com.cn",
    rating: 9,
  },
  {
    id: 2,
    name: "机械工业出版社",
    contactPerson: "李经理",
    phone: "010-87654321",
    email: "contact@cmpedu.com",
    rating: 8,
  },
  {
    id: 3,
    name: "电子工业出版社",
    contactPerson: "王经理",
    phone: "010-11223344",
    email: "contact@phei.com.cn",
    rating: 8,
  },
];

// 测试连接
app.get("/api/test/connection", (req, res) => {
  res.json({
    status: "success",
    message: "后端服务连接正常",
    timestamp: new Date().toISOString(),
    server: "Node.js Demo Server",
  });
});

// 数据库状态
app.get("/api/database/status", (req, res) => {
  res.json({
    databaseConnection: "connected",
    status: "success",
    userCount: users.length,
    bookCount: books.length,
    orderCount: orders.length,
    supplierCount: suppliers.length,
    userRepository: "working",
    bookRepository: "working",
    orderRepository: "working",
    supplierRepository: "working",
  });
});

// 用户管理API
app.get("/api/users", (req, res) => {
  res.json(users);
});

app.post("/api/users", (req, res) => {
  const newUser = {
    id: users.length + 1,
    ...req.body,
    isActive: true,
  };
  users.push(newUser);
  res.json(newUser);
});

app.put("/api/users/:id", (req, res) => {
  const id = parseInt(req.params.id);
  const userIndex = users.findIndex((u) => u.id === id);
  if (userIndex !== -1) {
    users[userIndex] = { ...users[userIndex], ...req.body };
    res.json(users[userIndex]);
  } else {
    res.status(404).json({ error: "用户不存在" });
  }
});

app.delete("/api/users/:id", (req, res) => {
  const id = parseInt(req.params.id);
  const userIndex = users.findIndex((u) => u.id === id);
  if (userIndex !== -1) {
    users.splice(userIndex, 1);
    res.json({ message: "用户删除成功" });
  } else {
    res.status(404).json({ error: "用户不存在" });
  }
});

// 图书管理API
app.get("/api/books", (req, res) => {
  res.json(books);
});

app.post("/api/books", (req, res) => {
  const newBook = {
    id: books.length + 1,
    ...req.body,
  };
  books.push(newBook);
  res.json(newBook);
});

app.put("/api/books/:id", (req, res) => {
  const id = parseInt(req.params.id);
  const bookIndex = books.findIndex((b) => b.id === id);
  if (bookIndex !== -1) {
    books[bookIndex] = { ...books[bookIndex], ...req.body };
    res.json(books[bookIndex]);
  } else {
    res.status(404).json({ error: "图书不存在" });
  }
});

app.delete("/api/books/:id", (req, res) => {
  const id = parseInt(req.params.id);
  const bookIndex = books.findIndex((b) => b.id === id);
  if (bookIndex !== -1) {
    books.splice(bookIndex, 1);
    res.json({ message: "图书删除成功" });
  } else {
    res.status(404).json({ error: "图书不存在" });
  }
});

// 订单管理API
app.get("/api/orders", (req, res) => {
  res.json(orders);
});

app.post("/api/orders", (req, res) => {
  const newOrder = {
    id: orders.length + 1,
    orderDate: new Date().toISOString().split("T")[0],
    ...req.body,
  };
  orders.push(newOrder);
  res.json(newOrder);
});

// 购物车API
app.get("/api/cart/:userId", (req, res) => {
  const userId = parseInt(req.params.userId);
  res.json({
    id: 1,
    userId: userId,
    items: [
      {
        id: 1,
        bookId: 1,
        bookTitle: "Java核心技术",
        quantity: 2,
        unitPrice: 89.0,
        totalPrice: 178.0,
      },
      {
        id: 2,
        bookId: 2,
        bookTitle: "Python编程：从入门到实践",
        quantity: 1,
        unitPrice: 79.0,
        totalPrice: 79.0,
      },
    ],
    totalAmount: 257.0,
  });
});

// 供应商管理API
app.get("/api/suppliers", (req, res) => {
  res.json(suppliers);
});

app.post("/api/suppliers", (req, res) => {
  const newSupplier = {
    id: suppliers.length + 1,
    ...req.body,
  };
  suppliers.push(newSupplier);
  res.json(newSupplier);
});

// 数据分析API
app.get("/api/analytics/sales", (req, res) => {
  res.json({
    totalSales: 267.0,
    orderCount: 2,
    averageOrderValue: 133.5,
    topSellingBooks: [
      { title: "Java核心技术", sales: 2 },
      { title: "Python编程：从入门到实践", sales: 1 },
    ],
  });
});

app.get("/api/analytics/users", (req, res) => {
  res.json({
    totalUsers: users.length,
    activeUsers: users.filter((u) => u.isActive).length,
    newUsersThisMonth: 1,
    userGrowth: 33.33,
  });
});

app.get("/api/analytics/inventory", (req, res) => {
  res.json({
    totalBooks: books.length,
    totalStock: books.reduce((sum, book) => sum + book.stock, 0),
    lowStockBooks: books.filter((book) => book.stock < 50).length,
    outOfStockBooks: books.filter((book) => book.stock === 0).length,
  });
});

// 启动服务器
app.listen(port, () => {
  console.log(`🚀 演示服务器已启动！`);
  console.log(`📱 前端地址: http://localhost:3000`);
  console.log(`🔧 后端API: http://localhost:${port}`);
  console.log(`📊 数据库状态: http://localhost:${port}/api/database/status`);
  console.log(`🔗 连接测试: http://localhost:${port}/api/test/connection`);
  console.log(
    `\n💡 提示：这是一个演示服务器，数据存储在内存中，重启后数据会重置`
  );
  console.log(
    `💡 要使用完整的Spring Boot后端，请安装Maven并运行: mvn spring-boot:run`
  );
});
