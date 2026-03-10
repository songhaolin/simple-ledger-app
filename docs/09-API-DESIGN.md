# API 设计

> _简账 - RESTful API 接口设计_
> _创建者：菜菜子 🤖 | 创建时间：2026-03-10_

---

## 📋 目录

1. [通用规范](#通用规范)
2. [接口列表](#接口列表)
3. [响应格式](#响应格式)
4. [错误码](#错误码)

---

## 🔧 通用规范

### Base URL

```
开发环境: http://localhost:8080/api/v1
生产环境: https://api.simple-ledger.com/api/v1
```

---

### 认证方式

```http
Authorization: Bearer {token}
```

---

### 请求格式

```http
Content-Type: application/json
Accept: application/json
```

---

## 📝 接口列表

### 1. 用户模块

#### 1.1 注册

```http
POST /api/v1/users/register
```

**请求体**：
```json
{
  "phone": "13800138000",
  "password": "Password123!",
  "nickname": "张三"
}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "userId": "user_001",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

#### 1.2 登录

```http
POST /api/v1/users/login
```

**请求体**：
```json
{
  "phone": "13800138000",
  "password": "Password123!"
}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "userId": "user_001",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "nickname": "张三",
      "avatar": "https://..."
    }
  }
}
```

---

#### 1.3 获取用户信息

```http
GET /api/v1/users/me
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "userId": "user_001",
    "phone": "138****8000",
    "nickname": "张三",
    "avatar": "https://..."
  }
}
```

---

### 2. 账本模块

#### 2.1 获取账本列表

```http
GET /api/v1/ledgers
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": [
    {
      "ledgerId": "ledger_001",
      "name": "我的账本",
      "type": "personal",
      "ownerId": "user_001",
      "members": [...],
      "budget": 4000
    }
  ]
}
```

---

#### 2.2 创建账本

```http
POST /api/v1/ledgers
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "name": "家庭账本",
  "type": "family",
  "budget": 5000
}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "ledgerId": "ledger_002",
    "name": "家庭账本",
    "inviteCode": "ABC123"
  }
}
```

---

#### 2.3 邀请成员

```http
POST /api/v1/ledgers/:ledgerId/members
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "phone": "13900139000",
  "role": "member"
}
```

---

#### 2.4 加入账本

```http
POST /api/v1/ledgers/join/:inviteCode
Authorization: Bearer {token}
```

---

### 3. 账单模块

#### 3.1 获取账单列表

```http
GET /api/v1/transactions?ledgerId={ledgerId}&page=1&limit=20
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "list": [
      {
        "transactionId": "tx_001",
        "type": "expense",
        "amount": 35.00,
        "category": {
          "id": "cat_001",
          "name": "餐饮",
          "icon": "🍚"
        },
        "subcategory": "午餐",
        "date": "2026-03-10T12:00:00Z",
        "note": "公司楼下",
        "images": ["https://..."]
      }
    ],
    "total": 100,
    "page": 1,
    "limit": 20
  }
}
```

---

#### 3.2 创建账单

```http
POST /api/v1/transactions
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "ledgerId": "ledger_001",
  "type": "expense",
  "amount": 35.00,
  "categoryId": "cat_001",
  "subcategory": "午餐",
  "date": "2026-03-10T12:00:00Z",
  "note": "公司楼下",
  "images": ["https://..."]
}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "transactionId": "tx_001"
  }
}
```

---

#### 3.3 更新账单

```http
PUT /api/v1/transactions/:transactionId
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "amount": 40.00,
  "note": "公司楼下，加量"
}
```

---

#### 3.4 删除账单

```http
DELETE /api/v1/transactions/:transactionId
Authorization: Bearer {token}
```

---

### 4. 分类模块

#### 4.1 获取分类列表

```http
GET /api/v1/categories?type=expense
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": [
    {
      "categoryId": "cat_001",
      "parentId": null,
      "name": "餐饮",
      "icon": "🍚",
      "color": "#FF6B6B",
      "subcategories": [
        { "name": "早餐" },
        { "name": "午餐" }
      ]
    }
  ]
}
```

---

#### 4.2 创建分类

```http
POST /api/v1/categories
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "name": "宠物",
  "icon": "🐱",
  "color": "#FFB6C1",
  "type": "expense"
}
```

---

### 5. 统计模块

#### 5.1 获取月度统计

```http
GET /api/v1/statistics/monthly?ledgerId={ledgerId}&month=2026-03
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "income": 5678.90,
    "expense": 3456.78,
    "balance": 2222.12,
    "byCategory": [
      {
        "categoryId": "cat_001",
        "categoryName": "餐饮",
        "total": 1037.03,
        "percentage": 30
      }
    ],
    "dailyTrend": [
      { "date": "2026-03-01", "expense": 120.50 },
      { "date": "2026-03-02", "expense": 85.00 }
    ]
  }
}
```

---

### 6. 预算模块

#### 6.1 获取预算

```http
GET /api/v1/budgets?ledgerId={ledgerId}&month=2026-03
Authorization: Bearer {token}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "totalBudget": 4000,
    "totalSpent": 3456.78,
    "totalRemaining": 543.22,
    "byCategory": [
      {
        "categoryId": "cat_001",
        "categoryName": "餐饮",
        "budget": 1200,
        "spent": 1037.03,
        "remaining": 162.97,
        "percentage": 86.4
      }
    ]
  }
}
```

---

#### 6.2 设置预算

```http
POST /api/v1/budgets
Authorization: Bearer {token}
```

**请求体**：
```json
{
  "ledgerId": "ledger_001",
  "categoryId": null,  // null 表示总预算
  "amount": 4000,
  "month": "2026-03",
  "alertThreshold": 0.8,
  "alertEnabled": true
}
```

---

## 📦 响应格式

### 成功响应

```json
{
  "success": true,
  "data": { ... }
}
```

---

### 失败响应

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "错误描述"
  }
}
```

---

## 🔴 错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 手机号格式错误 |
| 1002 | 密码格式错误 |
| 1003 | 手机号已注册 |
| 1004 | 密码错误 |
| 1005 | Token 无效 |
| 2001 | 账本不存在 |
| 2002 | 无权限访问账本 |
| 2003 | 邀请码无效 |
| 3001 | 账单不存在 |
| 3002 | 分类不存在 |
| 4001 | 参数错误 |
| 5000 | 服务器错误 |

---

_API设计完成（简化版）_
