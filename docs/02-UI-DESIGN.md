# 🎨 UI 设计文档

> _简账 App 完整UI设计规范_
> _创建者：菜菜子 🤖 | 创建时间：2026-03-10_

---

## 📋 目录

1. [设计原则](#设计原则)
2. [色彩规范](#色彩规范)
3. [字体规范](#字体规范)
4. [间距规范](#间距规范)
5. [图标系统](#图标系统)
6. [页面设计](#页面设计)
7. [组件库](#组件库)
8. [交互规范](#交互规范)

---

## 🎯 设计原则

### 核心理念

1. **简约优先**
   - 界面简洁，信息密度适中
   - 避免复杂装饰，突出核心功能
   - 留白充足，呼吸感强

2. **效率至上**
   - 3步完成记账
   - 常用操作一键完成
   - 减少页面跳转

3. **清晰明确**
   - 层次分明，重点突出
   - 数据可视化直观
   - 反馈及时

4. **一致性**
   - 统一的视觉语言
   - 一致的交互模式
   - 统一的命名规范

---

## 🎨 色彩规范

### 主色调

```css
/* 品牌主色 - 醒目但不过分跳跃 */
--primary: #4ECDC4;       /* 青绿色 */
--primary-dark: #3BAEAC;
--primary-light: #6EDCD6;

/* 辅助色 */
--accent: #FF6B6B;         /* 珊瑚红 - 支出 */
--success: #10AC84;        /* 翠绿色 - 收入 */
--warning: #FFA502;        /* 橙色 - 预算警告 */
--error: #FF4757;          /* 红色 - 错误 */
```

---

### 分类色系

```css
/* 餐饮 */
--category-food: #FF6B6B;

/* 交通 */
--category-transport: #4ECDC4;

/* 购物 */
--category-shopping: #FFA502;

/* 娱乐 */
--category-entertainment: #A55EEA;

/* 居住 */
--category-housing: #F368E0;

/* 医疗 */
--category-medical: #0ABDE3;

/* 教育 */
--category-education: #10AC84;

/* 其他 */
--category-other: #C44569;
```

---

### 中性色

```css
/* 文字 */
--text-primary: #2D3436;    /* 主标题 */
--text-secondary: #636E72;   /* 副标题 */
--text-tertiary: #B2BEC3;   /* 辅助文字 */

/* 背景 */
--bg-primary: #FFFFFF;       /* 主背景 */
--bg-secondary: #F5F6FA;     /* 次背景 */
--bg-tertiary: #DFE4EA;     /* 分割线 */

/* 边框 */
--border-light: #DFE4EA;
--border-medium: #B2BEC3;
```

---

## ✏️ 字体规范

### 字体家族

```css
/* Android */
--font-family: 'Roboto', 'Noto Sans SC', sans-serif;

/* iOS */
--font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif;
```

---

### 字号规范

| 用途 | 字号 | 行高 | 举例 |
|------|------|------|------|
| H1 大标题 | 24px | 1.2 | 简账 |
| H2 标题 | 20px | 1.3 | 本月支出 |
| H3 小标题 | 18px | 1.4 | 餐饮 |
| Body 正文 | 16px | 1.5 | 午餐 |
| Body 小 | 14px | 1.6 | 备注 |
| Caption 辅助 | 12px | 1.6 | 2026-03-10 |

---

### 字重规范

```css
/* 常规 */
--font-regular: 400;

/* 中等 - 强调 */
--font-medium: 500;

/* 粗体 - 标题 */
--font-bold: 600;

/* 特粗 - 重点 */
--font-black: 700;
```

---

## 📏 间距规范

### 基础间距

```css
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 16px;
--spacing-lg: 24px;
--spacing-xl: 32px;
--spacing-xxl: 48px;
```

---

### 应用场景

| 场景 | 间距 | 说明 |
|------|------|------|
| 按钮内边距 | 12px 24px | 舒适点击区 |
| 卡片内边距 | 16px | 内容与边界 |
| 元素间距 | 12px | 相邻元素 |
| 页面边距 | 16px | 页面两侧 |
| 分组间距 | 24px | 不同分组 |

---

## 🎯 图标系统

### 尺寸规范

| 尺寸 | 用途 |
|------|------|
| 16px | 列表图标、按钮图标 |
| 20px | 输入框图标 |
| 24px | Tab 图标 |
| 32px | 大型图标 |
| 48px | 空状态图标 |

---

### 分类图标

| 分类 | Emoji | 说明 |
|------|--------|------|
| 餐饮 | 🍚 | 早餐、午餐、晚餐 |
| 交通 | 🚗 | 公交、地铁、打车 |
| 购物 | 🛍️ | 服装、日用品 |
| 娱乐 | 🎮 | 电影、游戏、旅游 |
| 居住 | 🏠 | 水电、房租 |
| 医疗 | 💊 | 买药、挂号 |
| 教育 | 📚 | 书籍、课程 |
| 其他 | 📦 | 其他支出 |

---

### 功能图标

| 图标 | 描述 |
|------|------|
| ➕ | 添加 |
| ✏️ | 编辑 |
| 🗑️ | 删除 |
| 🔙 | 返回 |
| 📊 | 统计 |
| ⚙️ | 设置 |
| 👤 | 个人 |
| 🔍 | 搜索 |

---

## 📱 页面设计

### 页面 1：首页（账本）

#### 布局结构

```
┌─────────────────────────────────┐
│  简账              🔔 👤      │  导航栏 (56px)
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐  │
│  │ 总资产：¥12,345.67      │  │  资产卡片 (120px)
│  │ 本月支出：¥3,456.78    │  │
│  │ 本月收入：¥5,678.90    │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 本月预算：¥4,000       │  │  预算进度 (60px)
│  │ ████████░░░░ 86%     │  │
│  └───────────────────────────┘  │
│                                 │
│  ───── 今日收支 ────           │  分组标题 (32px)
│                                 │
│  ┌───────────────────────────┐  │
│  │ 🍚 午餐        -35.00  │  │  账单项 (60px)
│  │ 12:00  公司楼下          │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 💰 工资        +8000.00│  │
│  │ 09:00  公司发放          │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 🚗 地铁         -5.00   │  │
│  │ 08:30  通勤              │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 🍚 晚餐        -45.00  │  │
│  │ 19:00  外卖              │  │
│  └───────────────────────────┘  │
│                                 │
│                                 │
│        ┌─────────────────┐      │
│        │      + 记一笔     │      │  浮动按钮 (56px)
│        └─────────────────┘      │
├─────────────────────────────────┤
│  📒账本  📊统计  🎯预算  👤我的  │  底部Tab (56px)
└─────────────────────────────────┘
```

---

#### 详细尺寸

```css
/* 导航栏 */
.navbar {
  height: 56px;
  padding: 0 16px;
  background: #FFFFFF;
  border-bottom: 1px solid #DFE4EA;
}

/* 资产卡片 */
.asset-card {
  padding: 16px;
  margin: 0 16px;
  background: linear-gradient(135deg, #4ECDC4 0%, #3BAEAC 100%);
  border-radius: 16px;
  color: #FFFFFF;
}

.asset-card .amount {
  font-size: 28px;
  font-weight: 600;
}

.asset-card .label {
  font-size: 14px;
  opacity: 0.8;
}

/* 预算进度 */
.budget-progress {
  height: 60px;
  padding: 12px 16px;
  margin: 12px 16px;
  background: #F5F6FA;
  border-radius: 12px;
}

.progress-bar {
  height: 8px;
  border-radius: 4px;
  background: #DFE4EA;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  background: #4ECDC4;
}

/* 账单项 */
.transaction-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #F5F6FA;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #F5F6FA;
  margin-right: 12px;
}

.transaction-info {
  flex: 1;
}

.transaction-category {
  font-size: 16px;
  font-weight: 500;
  color: #2D3436;
}

.transaction-note {
  font-size: 12px;
  color: #B2BEC3;
  margin-top: 2px;
}

.transaction-amount {
  font-size: 18px;
  font-weight: 600;
}

.transaction-amount.expense {
  color: #FF6B6B;
}

.transaction-amount.income {
  color: #10AC84;
}

/* 浮动按钮 */
.fab {
  position: fixed;
  bottom: 80px;
  right: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #4ECDC4;
  color: #FFFFFF;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.4);
}

/* 底部Tab */
.tabbar {
  height: 56px;
  background: #FFFFFF;
  border-top: 1px solid #DFE4EA;
  display: flex;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #B2BEC3;
}

.tab-item.active {
  color: #4ECDC4;
}

.tab-icon {
  font-size: 24px;
  margin-bottom: 4px;
}
```

---

### 交互规范

#### 记账按钮点击

```
点击浮动按钮
  ↓
放大动画（scale: 1.2 → 1.0）
  ↓
路由跳转：/record
```

#### 账单项点击

```
点击账单项
  ↓
高亮动画（opacity: 0.7 → 1.0）
  ↓
路由跳转：/transaction/:id
```

#### 下拉刷新

```
下拉首页
  ↓
显示加载指示器
  ↓
刷新数据
  ↓
恢复列表
```

---

### 状态设计

#### 空状态

```
┌─────────────────────────────────┐
│                                 │
│         📒                     │
│                                 │
│      还没有记账记录            │
│                                 │
│   点击下方按钮开始记账        │
│                                 │
│        ┌─────────────────┐      │
│        │      + 记一笔     │      │
│        └─────────────────┘      │
└─────────────────────────────────┘
```

#### 加载状态

```
┌─────────────────────────────────┐
│                                 │
│         📒                     │
│                                 │
│      加载中...                 │
│                                 │
│      🔄 Spinner              │
│                                 │
└─────────────────────────────────┘
```

#### 错误状态

```
┌─────────────────────────────────┐
│                                 │
│         ❌                     │
│                                 │
│      加载失败                 │
│                                 │
│      请检查网络后重试        │
│                                 │
│      [重试]                   │
└─────────────────────────────────┘
```

---

_（第1部分完成，正在进行第2部分...）_
