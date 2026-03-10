# 数据库设计

> _简账 - MongoDB 数据库结构设计_
> _创建者：菜菜子 🤖 | 创建时间：2026-03-10_

---

## 📋 目录

1. [集合总览](#集合总览)
2. [详细设计](#详细设计)
3. [索引策略](#索引策略)
4. [数据关系](#数据关系)

---

## 🎯 集合总览

| 集合名 | 用途 | 主要字段 |
|--------|------|---------|
| `users` | 用户信息 | phone, passwordHash, nickname |
| `ledgers` | 账本 | name, type, ownerId, budget |
| `transactions` | 账单 | amount, categoryId, ledgerId, userId |
| `categories` | 分类 | name, icon, color, type |
| `budgets` | 预算 | ledgerId, categoryId, amount, month |

---

## 📝 详细设计

### 1. users（用户）

```javascript
{
  _id: ObjectId,              // 用户ID
  phone: String,              // 手机号（唯一）
  passwordHash: String,        // 密码哈希
  nickname: String,           // 昵称
  avatar: String,             // 头像URL
  createdAt: Date,            // 创建时间
  updatedAt: Date,            // 更新时间
  isActive: Boolean,          // 是否激活
  lastLoginAt: Date          // 最后登录时间
}
```

**索引**：
- `phone` (unique)
- `_id` (primary)

---

### 2. ledgers（账本）

```javascript
{
  _id: ObjectId,              // 账本ID
  name: String,              // 账本名称
  type: String,              // 类型: "personal" | "family"
  ownerId: ObjectId,          // 创建者ID（关联users）
  members: [                 // 成员列表
    {
      userId: ObjectId,        // 用户ID
      role: String,          // 角色: "owner" | "member" | "viewer"
      joinedAt: Date         // 加入时间
    }
  ],
  budget: Number,            // 总预算（可选）
  currency: String,         // 货币（默认"CNY"）
  inviteCode: String,        // 邀请码（家庭账本）
  createdAt: Date,
  updatedAt: Date
}
```

**索引**：
- `ownerId`
- `type`
- `members.userId`

---

### 3. transactions（账单）

```javascript
{
  _id: ObjectId,              // 账单ID
  ledgerId: ObjectId,         // 账本ID（关联ledgers）
  userId: ObjectId,          // 记账用户ID
  type: String,              // 类型: "income" | "expense" | "transfer"
  amount: Number,            // 金额
  categoryId: ObjectId,      // 分类ID（关联categories）
  categoryName: String,       // 分类名称（冗余，提升查询）
  subcategory: String,       // 二级分类
  date: Date,               // 日期
  note: String,             // 备注
  images: [String],         // 图片URL列表
  createdAt: Date,
  updatedAt: Date,
  isDeleted: Boolean        // 软删除标记
}
```

**索引**：
- `ledgerId` + `date` (复合索引，查询账单列表）
- `userId`
- `categoryId`
- `date`

---

### 4. categories（分类）

```javascript
{
  _id: ObjectId,              // 分类ID
  parentId: ObjectId,        // 父分类ID（null表示一级分类）
  name: String,              // 分类名称
  icon: String,             // 图标（emoji）
  color: String,            // 颜色（hex）
  type: String,             // 类型: "income" | "expense"
  isDefault: Boolean,        // 是否预设分类
  userId: ObjectId,          // 所属用户（null表示全局分类）
  sortOrder: Number,        // 排序
  createdAt: Date
}
```

**索引**：
- `parentId`
- `type`
- `userId`
- `name`

---

### 5. budgets（预算）

```javascript
{
  _id: ObjectId,              // 预算ID
  ledgerId: ObjectId,         // 账本ID
  categoryId: ObjectId,      // 分类ID（null表示总预算）
  amount: Number,            // 预算金额
  month: String,            // 月份（格式: "2026-03"）
  alertThreshold: Number,     // 提醒阈值（0.8表示80%）
  alertEnabled: Boolean,      // 是否启用提醒
  createdAt: Date,
  updatedAt: Date
}
```

**索引**：
- `ledgerId` + `month` (复合索引）
- `categoryId`

---

## 🔍 索引策略

### 1. 用户相关

```javascript
// users 集合
db.users.createIndex({ phone: 1 }, { unique: true })
db.users.createIndex({ createdAt: -1 })
```

---

### 2. 账本相关

```javascript
// ledgers 集合
db.ledgers.createIndex({ ownerId: 1 })
db.ledgers.createIndex({ type: 1 })
db.ledgers.createIndex({ "members.userId": 1 })
db.ledgers.createIndex({ inviteCode: 1 }, { sparse: true })
```

---

### 3. 账单相关（关键）

```javascript
// transactions 集合
db.transactions.createIndex({ ledgerId: 1, date: -1 })
db.transactions.createIndex({ userId: 1 })
db.transactions.createIndex({ categoryId: 1 })
db.transactions.createIndex({ date: -1 })
```

**说明**：
- `{ ledgerId: 1, date: -1 }` 复合索引用于查询账单列表（最常用）
- `{ date: -1 }` 用于时间范围查询

---

### 4. 分类相关

```javascript
// categories 集合
db.categories.createIndex({ parentId: 1 })
db.categories.createIndex({ type: 1 })
db.categories.createIndex({ userId: 1 })
db.categories.createIndex({ type: 1, sortOrder: 1 })
```

---

### 5. 预算相关

```javascript
// budgets 集合
db.budgets.createIndex({ ledgerId: 1, month: 1 })
db.budgets.createIndex({ categoryId: 1 })
```

---

## 🔗 数据关系

### 关系图

```
User (用户)
  ↓ 1:N
Ledger (账本)
  ↓ 1:N
  ├─ Transaction (账单)
  ├─ Budget (预算)
  └─ Member (成员)
    ↓ N:1
  User (用户)
```

---

### 关系详细说明

#### 1. 用户 ↔ 账本

```
User._id = Ledger.ownerId
  (一个用户可以创建多个账本）

User._id ∈ Ledger.members[*].userId
  (一个用户可以加入多个账本）
```

---

#### 2. 账本 ↔ 账单

```
Ledger._id = Transaction.ledgerId
  (一个账本包含多笔记账）

Ledger.members[*].userId = Transaction.userId
  (成员可以给账本记账）
```

---

#### 3. 账本 ↔ 预算

```
Ledger._id = Budget.ledgerId
  (一个账本可以设置多个预算）

Ledger._id = Budget.ledgerId + Budget.month
  (按账本和月份查询预算）
```

---

#### 4. 分类 ↔ 账单

```
Category._id = Transaction.categoryId
  (一笔记账属于一个分类）

Category._id = Budget.categoryId
  (按分类设置预算）
```

---

## 📊 查询场景

### 场景1：首页账单列表

```javascript
// 查询指定账本的账单，按时间倒序
db.transactions.find({
  ledgerId: ledgerId,
  isDeleted: false
}).sort({ date: -1 }).limit(20)
```

**使用索引**：`{ ledgerId: 1, date: -1 }`

---

### 场景2：本月统计数据

```javascript
// 查询本月账单
const startDate = new Date(2026, 2, 1); // 3月1日
const endDate = new Date(2026, 3, 1);   // 4月1日

db.transactions.aggregate([
  {
    $match: {
      ledgerId: ledgerId,
      date: { $gte: startDate, $lt: endDate },
      isDeleted: false
    }
  },
  {
    $group: {
      _id: "$categoryId",
      totalAmount: { $sum: "$amount" },
      count: { $sum: 1 }
    }
  }
])
```

---

### 场景3：预算使用情况

```javascript
// 查询本月预算和使用情况
const month = "2026-03";

// 1. 获取预算
db.budgets.find({
  ledgerId: ledgerId,
  month: month
})

// 2. 计算已用金额
db.transactions.aggregate([
  {
    $match: {
      ledgerId: ledgerId,
      date: { $gte: startDate, $lt: endDate },
      type: "expense",
      isDeleted: false
    }
  },
  {
    $group: {
      _id: "$categoryId",
      spent: { $sum: "$amount" }
    }
  }
])
```

---

## 💾 数据初始化

### 预设分类数据

```javascript
const defaultCategories = [
  // 支出分类
  {
    parentId: null,
    name: "餐饮",
    icon: "🍚",
    color: "#FF6B6B",
    type: "expense",
    isDefault: true,
    sortOrder: 1,
    children: [
      { name: "早餐", icon: "🥞" },
      { name: "午餐", icon: "🍜" },
      { name: "晚餐", icon: "🍽️" },
      { name: "外卖", icon: "🥡" },
      { name: "零食", icon: "🍪" }
    ]
  },
  {
    parentId: null,
    name: "交通",
    icon: "🚗",
    color: "#4ECDC4",
    type: "expense",
    isDefault: true,
    sortOrder: 2,
    children: [
      { name: "公交", icon: "🚌" },
      { name: "地铁", icon: "🚇" },
      { name: "打车", icon: "🚕" },
      { name: "加油", icon: "⛽" },
      { name: "停车", icon: "🅿️" }
    ]
  },
  {
    parentId: null,
    name: "购物",
    icon: "🛍️",
    color: "#FFA502",
    type: "expense",
    isDefault: true,
    sortOrder: 3,
    children: [
      { name: "服装", icon: "👗" },
      { name: "日用品", icon: "🧴" },
      { name: "数码", icon: "📱" },
      { name: "化妆品", icon: "💄" }
    ]
  },
  {
    parentId: null,
    name: "娱乐",
    icon: "🎮",
    color: "#A55EEA",
    type: "expense",
    isDefault: true,
    sortOrder: 4,
    children: [
      { name: "电影", icon: "🎬" },
      { name: "游戏", icon: "🎮" },
      { name: "旅游", icon: "✈️" },
      { name: "KTV", icon: "🎤" }
    ]
  },
  {
    parentId: null,
    name: "居住",
    icon: "🏠",
    color: "#F368E0",
    type: "expense",
    isDefault: true,
    sortOrder: 5,
    children: [
      { name: "水电", icon: "💡" },
      { name: "燃气", icon: "🔥" },
      { name: "物业", icon: "🏢" },
      { name: "房租", icon: "🏠" }
    ]
  },
  {
    parentId: null,
    name: "医疗",
    icon: "💊",
    color: "#0ABDE3",
    type: "expense",
    isDefault: true,
    sortOrder: 6,
    children: [
      { name: "买药", icon: "💊" },
      { name: "挂号", icon: "🏥" },
      { name: "检查", icon: "🩺" }
    ]
  },
  {
    parentId: null,
    name: "教育",
    icon: "📚",
    color: "#10AC84",
    type: "expense",
    isDefault: true,
    sortOrder: 7,
    children: [
      { name: "书籍", icon: "📖" },
      { name: "课程", icon: "🎓" },
      { name: "培训", icon: "📝" }
    ]
  },
  {
    parentId: null,
    name: "其他",
    icon: "📦",
    color: "#C44569",
    type: "expense",
    isDefault: true,
    sortOrder: 8,
    children: [
      { name: "打赏", icon: "💰" },
      { name: "红包", icon: "🧧" },
      { name: "罚款", icon: "📋" }
    ]
  },

  // 收入分类
  {
    parentId: null,
    name: "工资",
    icon: "💰",
    color: "#10AC84",
    type: "income",
    isDefault: true,
    sortOrder: 1
  },
  {
    parentId: null,
    name: "奖金",
    icon: "🎁",
    color: "#10AC84",
    type: "income",
    isDefault: true,
    sortOrder: 2
  },
  {
    parentId: null,
    name: "理财",
    icon: "📈",
    color: "#10AC84",
    type: "income",
    isDefault: true,
    sortOrder: 3
  },
  {
    parentId: null,
    name: "其他",
    icon: "💵",
    color: "#10AC84",
    type: "income",
    isDefault: true,
    sortOrder: 4
  }
]

// 初始化分类
defaultCategories.forEach(cat => {
  const parentId = db.categories.insertOne({
    ...cat,
    userId: null
  }).insertedId

  // 插入子分类
  if (cat.children) {
    cat.children.forEach(sub => {
      db.categories.insertOne({
        name: sub.name,
        icon: sub.icon,
        color: cat.color,
        type: cat.type,
        isDefault: true,
        parentId: parentId,
        userId: null
      })
    })
  }
})
```

---

## 🔒 数据安全

### 1. 密码存储

```javascript
// 使用 bcrypt 加密
const bcrypt = require('bcrypt')

// 注册时
const passwordHash = await bcrypt.hash(plainPassword, 10)

// 登录时
const isValid = await bcrypt.compare(plainPassword, passwordHash)
```

---

### 2. 软删除

```javascript
// 不物理删除，只标记
db.transactions.updateOne(
  { _id: transactionId },
  { $set: { isDeleted: true, updatedAt: new Date() } }
)

// 查询时过滤
db.transactions.find({ isDeleted: false })
```

---

### 3. 数据备份

```javascript
// 定期备份脚本
const { exec } = require('child_process')

const backup = () => {
  const date = new Date().toISOString().split('T')[0]
  const filename = `backup-${date}.gz`

  exec(`mongodump --db ledger --archive=${filename} --gzip`, (error) => {
    if (error) console.error('Backup failed', error)
    else console.log('Backup successful', filename)
  })
}

// 每天凌晨2点备份
setInterval(backup, 24 * 60 * 60 * 1000)
```

---

_数据库设计完成_
