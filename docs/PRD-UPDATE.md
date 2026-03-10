# PRD 更新说明

> _根据用户反馈优化产品设计_
> _创建者：菜菜子 🤖 | 更新时间：2026-03-10_

---

## 📋 主要更新

### 1. MVP 功能调整

**移到 MVP**：
- ✅ 家庭账本（统一统计）
- ✅ 预算管理（总预算 + 分类预算）

**保持不变**：
- ✅ 个人账本（独立记账）
- ✅ 基础记账
- ✅ 分类管理
- ✅ 列表展示
- ✅ 基础统计
- ✅ 用户系统
- ✅ 数据同步

---

### 2. 首页 UI 重新设计（仿鲨鱼记账）

#### 核心变化

**底部导航**：
- ✅ 中间：+ 记账图标（浮动按钮）
- ✅ 左右：其他 Tab

**顶部菜单**：
- ✅ 账本切换（个人/家庭）
- ✅ 搜索
- ✅ 设置

**主体内容**：
- ✅ 顶部：本月收支卡片
- ✅ 主体：账单详细信息列表
- ✅ 支持分组（按日期）

---

### 3. 记账页面交互优化

#### 新的交互流程

```
进入记账页
  ↓
展示所有分类（4列网格）
  ↓
点击分类
  ↓
自动弹出数字键盘
  ↓
输入金额（支持简单加减法）
  ↓
点击备注
  ↓
切换到输入法键盘
  ↓
输入文字
  ↓
保存
```

#### 布局调整

**分类在顶部**：
- 8列×3行网格
- 滚动浏览

**金额和备注在底部**：
- 数字键盘自动弹出
- 备注点击呼出

---

## 🎯 首页详细设计（仿鲨鱼记账）

### 布局结构

```
┌─────────────────────────────────┐
│  📒 我的账本     🔍 ⚙️    │  顶部菜单 (56px)
├─────────────────────────────────┤
│                                 │
│  ┌──────────────────────┐     │  收支卡片 (80px)
│  │ 3月               │     │
│  │ 支出 ¥3,456.78    │     │
│  │ 收入 ¥5,678.90    │     │
│  │ 结余 ¥2,222.12    │     │
│  └──────────────────────┘     │
│                                 │
│  ┌──────────────────────┐     │  预算提醒 (可选)
│  │ 📢 本月预算提醒    │     │
│  │ 已用 86%，注意控制 │     │
│  └──────────────────────┘     │
│                                 │
│  ───── 今日 2026-03-10 ────  │  日期分组
│                                 │
│  ┌──────────────────────┐     │  账单项 (56px)
│  │ 🍚 餐饮            │     │
│  │ 午餐               │     │  分类 + 备注
│  │            -¥35.00   │     │  金额
│  │ 12:00              │     │  时间
│  └──────────────────────┘     │
│                                 │
│  ┌──────────────────────┐     │
│  │ 🚗 交通            │     │
│  │ 地铁               │     │
│  │             -¥5.00 │     │
│  │ 08:30              │     │
│  └──────────────────────┘     │
│                                 │
│  ───── 昨日 2026-03-09 ────  │
│                                 │
│  ┌──────────────────────┐     │
│  │ 🛍️ 购物           │     │
│  │ 日用品             │     │
│  │           -¥128.00 │     │
│  │ 19:30              │     │
│  └──────────────────────┘     │
│                                 │
│  ┌──────────────────────┐     │
│  │ 💰 工资            │     │
│  │ 3月工资            │     │
│  │           +¥8000.00│     │
│  │ 09:00              │     │
│  └──────────────────────┘     │
│                                 │
├─────────────────────────────────┤
│  🏠首页  📊统计  🎯预算  👤我的  │  底部 Tab (56px)
│          ↑           ↑                  │
│          └─ 中间有间隔（+号放这里）
└─────────────────────────────────┘
```

---

### 样式定义

#### 顶部菜单

```css
.topbar {
  height: 56px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border-bottom: 1px solid #F5F6FA;
}

.ledger-selector {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #2D3436;
}

.ledger-selector .icon {
  margin-right: 8px;
}

.menu-icons {
  display: flex;
  gap: 16px;
}

.menu-icon {
  font-size: 20px;
  color: #636E72;
}
```

---

#### 收支卡片

```css
.month-card {
  padding: 16px;
  margin: 12px 16px;
  background: linear-gradient(135deg, #4ECDC4 0%, #3BAEAC 100%);
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3);
}

.month-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 12px;
}

.month-stats {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 4px;
}

.stat-amount {
  font-size: 20px;
  font-weight: 600;
  color: #FFFFFF;
}

.stat-amount.balance {
  font-size: 24px;
}
```

---

#### 账单项

```css
.date-header {
  padding: 8px 16px;
  font-size: 12px;
  color: #636E72;
  background: #F5F6FA;
}

.transaction-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #FFFFFF;
  border-bottom: 1px solid #F5F6FA;
}

.transaction-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #F5F6FA;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.transaction-icon {
  font-size: 24px;
}

.transaction-main {
  flex: 1;
}

.transaction-category {
  font-size: 16px;
  font-weight: 500;
  color: #2D3436;
  margin-bottom: 4px;
}

.transaction-note {
  font-size: 12px;
  color: #B2BEC3;
}

.transaction-right {
  text-align: right;
}

.transaction-amount {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.transaction-amount.expense {
  color: #FF6B6B;
}

.transaction-amount.income {
  color: #10AC84;
}

.transaction-time {
  font-size: 12px;
  color: #B2BEC3;
}
```

---

#### 底部 Tab（带中间+号）

```css
.tabbar {
  height: 56px;
  background: #FFFFFF;
  border-top: 1px solid #DFE4EA;
  display: flex;
  align-items: center;
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #B2BEC3;
  height: 100%;
}

.tabbar-item.active {
  color: #4ECDC4;
}

.tabbar-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

/* 中间间隔 */
.tabbar-spacer {
  width: 64px;
}

/* 浮动记账按钮 */
.record-fab {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #4ECDC4;
  color: #FFFFFF;
  font-size: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(78, 205, 196, 0.5);
  border: 4px solid #FFFFFF;
  cursor: pointer;
  transition: all 0.2s;
}

.record-fab:hover {
  transform: translateX(-50%) scale(1.1);
}

.record-fab:active {
  transform: translateX(-50%) scale(0.95);
}
```

---

## 🎯 记账页面详细设计（优化版）

### 布局结构

```
┌─────────────────────────────────┐
│  ← 记一笔              完成   │  导航栏 (56px)
├─────────────────────────────────┤
│                                 │
│  收入   支出   转账          │  类型切换 (48px)
│   ✓                           │
├─────────────────────────────────┤
│                                 │
│  ┌──┬──┬──┬──┬──┬──┬──┬──┐│  分类网格（顶部）
│  │🍚│🚗│🛍️│🎮│🏠│💊│📚│📦││
│  ├──┴──┴──┴──┴──┴──┴──┴──┤│
│  │餐饮│交通│购物│娱乐│居住│医疗│教育│其他│
│  └───┬──┴───┬┴───┬───┴───┬──┴───┬┘  │
│      │    │    │    │    │    │     │
│  ┌───┴──┐┌───┴──┐┌───┴──┐┌──┴──┐┌──┴──┐ │
│  │🥞 ││🚌   ││👗   ││🎬 ││💡   │ │
│  │早餐 ││公交  ││服装  ││电影 ││水电  │ │
│  └───┬──┘└───┬──┘└───┬──┘└──┬──┘└───┬──┘ │
└──────┴────────┴───────┴───────┴───────┘
│  ▲可滚动查看所有分类            │
├─────────────────────────────────┤
│  ¥ 35.00               │  金额显示 (56px)
├─────────────────────────────────┤
│  ┌──┬──┬──┐  ┌──┬──┬──┐  │  数字键盘 (280px)
│  │ 1│ 2│ 3│  │ -│ /│ ← │  │  支持简单加减法
│  ├──┼──┼──┤  ├──┼──┼──┤  │
│  │ 4│ 5│ 6│  │ +│ ×│ % │  │
│  ├──┼──┼──┤  ├──┼──┼──┤  │
│  │ 7│ 8│ 9│  │ .│ ¥│  ⌫│  │  ⌫退格
│  ├──┼──┼──┤  └──┴──┴──┘  │
│  │+/-│ 0│ ⏱️               │  │  ⏱️日期
│  └──┴──┴──┴───────────────┘  │
├─────────────────────────────────┤
│  备注：公司楼下            │  备注输入 (48px)
│  ┌──────────────────────┐   │
│  │ 午餐，很好吃       │   │  输入后保存
│  └──────────────────────┘   │
├─────────────────────────────────┤
│  日期：今天  >  图片：📷 +  │  其他选项 (48px)
├─────────────────────────────────┤
│         保存                    │  保存按钮 (56px)
└─────────────────────────────────┘
```

---

### 交互流程

#### 进入记账页面

```
进入页面
  ↓
分类区域自动聚焦第一个
  ↓
等待用户选择分类
  ↓
（金额和备注隐藏）
```

---

#### 选择分类

```
点击分类图标
  ↓
分类高亮
  ↓
底部自动弹出数字键盘
  ↓
金额输入框获得焦点
  ↓
等待用户输入
```

---

#### 输入金额

```
数字键盘默认弹出
  ↓
输入数字
  ↓
实时显示：¥XX.XX
  ↓
支持简单运算：
  - 点击 + - × /
  - 点击第二个数字
  - 计算结果
  - 示例：10 + 5 = 15
  ↓
点击 ⌫ 退格
```

---

#### 输入备注

```
点击备注输入框
  ↓
数字键盘收起
  ↓
呼出输入法键盘
  ↓
输入文字
  ↓
点击键盘"完成"
  ↓
输入法收起
  ↓
（数字键盘不自动弹出）
```

---

#### 保存

```
点击保存
  ↓
验证：
  - 已选择分类 ✓
  - 金额 > 0 ✓
  ↓
提交数据
  ↓
返回首页
```

---

### 样式定义

#### 分类网格

```css
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px;
  max-height: 240px;
  overflow-y: auto;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  border-radius: 12px;
  background: #F5F6FA;
  cursor: pointer;
  transition: all 0.2s;
}

.category-item.selected {
  background: #4ECDC4;
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3);
}

.category-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 10px;
  color: #636E72;
}

.category-item.selected .category-name {
  color: #FFFFFF;
}
```

---

#### 数字键盘

```css
.amount-display {
  padding: 16px;
  text-align: right;
  background: #FFFFFF;
  border-top: 1px solid #F5F6FA;
}

.amount-input {
  font-size: 48px;
  font-weight: 600;
  color: #2D3436;
}

.numeric-keyboard {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
  padding: 12px;
  background: #F5F6FA;
}

.key {
  height: 56px;
  border-radius: 8px;
  background: #FFFFFF;
  font-size: 20px;
  font-weight: 500;
  color: #2D3436;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.1s;
}

.key:active {
  background: #E8ECF1;
  transform: scale(0.95);
}

.key.operator {
  background: #4ECDC4;
  color: #FFFFFF;
}

.key.operator:active {
  background: #3BAEAC;
}

.key.date-key {
  background: #F5F6FA;
  color: #636E72;
}
```

---

#### 备注输入

```css
.note-section {
  padding: 12px 16px;
  background: #FFFFFF;
  border-top: 1px solid #F5F6FA;
}

.note-label {
  font-size: 14px;
  color: #636E72;
  margin-bottom: 8px;
}

.note-input {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  color: #2D3436;
  background: #F5F6FA;
  border: none;
  border-radius: 12px;
  min-height: 48px;
}

.note-input:focus {
  background: #E8ECF1;
  outline: none;
}
```

---

## 📋 需要合并到原文档的内容

### 更新 docs/01-PRD.md

1. **MVP 定义**：添加家庭账本、预算管理
2. **功能范围**：调整 V1.0 内容

### 替换 docs/02-UI-DESIGN.md

1. **首页设计**：完全替换为仿鲨鱼记账版本
2. **记账页面**：完全替换为优化版

---

_更新说明完成，准备提交_
