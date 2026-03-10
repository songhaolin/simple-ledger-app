# 其他页面 UI 设计

> _预算页面 + 个人中心 + 组件库（简化版）_
> _创建者：菜菜子 🤖_

---

## 预算页面

### 布局

```
┌─────────────────────────────────┐
│  预算                     +   │  导航栏
├─────────────────────────────────┤
│                                 │
│  本月预算：¥4,000           │  总预算卡片
│  已用：¥3,456 (86%)        │
│  ━━━━━━━━━━━━━━━━━━ 86%  │
│  剩余：¥544                │
├─────────────────────────────────┤
│  ───── 分类预算 ────         │
│                                 │
│  🍚 餐饮                     │
│  预算：¥1,200  已用：¥1,037│
│  ━━━━━━━━━━━━━━━━━ 86%      │
│                                 │
│  🚗 交通                     │
│  预算：¥500    已用：¥691  │
│  ━━━━━━━━━━━━━━━━━ 138% 🔴│
│                                 │
│  🛍️ 购物                     │
│  预算：¥800    已用：¥519  │
│  ━━━━━━━━━━━━━━━ 65%       │
└─────────────────────────────────┘
```

### 核心样式
- 预算卡片：白色背景 + 圆角
- 进度条：绿色(<80%) / 黄色(80-100%) / 红色(>100%)
- 超支警告：红色标识 + 图标

---

## 个人中心

### 布局

```
┌─────────────────────────────────┐
│  我的                          │  导航栏
├─────────────────────────────────┤
│                                 │
│  ┌──────────────────────┐     │  用户卡片
│  │ 👤 张三            │     │
│  │ 138****8000        │     │
│  └──────────────────────┘     │
│                                 │
│  ───── 我的账本 ────         │
│                                 │
│  📒 我的账本 (当前)          │
│  🏠 家庭账本                 │
│  ➕ 添加账本                 │
│                                 │
│  ───── 功能 ────              │
│  📊 报表导出                 │
│  📅 定期提醒                 │
│  💾 数据备份                 │
│                                 │
│  ───── 其他 ────              │
│  ⚙️ 设置                     │
│  ❓ 帮助与反馈               │
│  ℹ️ 关于我们                 │
└─────────────────────────────────┘
```

### 核心样式
- 用户卡片：头像 + 昵称 + 手机号
- 列表项：图标 + 文字 + 箭头
- 分隔线：浅灰色

---

## 组件库（核心组件）

### 1. 按钮

```css
/* 主要按钮 */
.btn-primary {
  background: #4ECDC4;
  color: white;
  height: 48px;
  border-radius: 12px;
}

/* 次要按钮 */
.btn-secondary {
  background: #F5F6FA;
  color: #2D3436;
  height: 48px;
  border-radius: 12px;
}

/* 危险按钮 */
.btn-danger {
  background: #FF4757;
  color: white;
  height: 48px;
  border-radius: 12px;
}
```

### 2. 卡片

```css
.card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
```

### 3. 输入框

```css
.input {
  width: 100%;
  height: 48px;
  padding: 12px;
  border: 1px solid #DFE4EA;
  border-radius: 12px;
  font-size: 16px;
}

.input:focus {
  border-color: #4ECDC4;
  outline: none;
}
```

### 4. 对话框

```css
.modal {
  background: rgba(0,0,0,0.5);
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin: 16px;
  max-width: 400px;
}
```

### 5. Toast 提示

```css
.toast {
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  position: fixed;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
}
```

---

## 交互规范

### 页面切换
- 点击底部 Tab：路由切换 + 动画
- 返回按钮：返回上一页

### 手势
- 下拉刷新：首页、列表页
- 左滑删除：账单列表（可选）

### 反馈
- 点击：按钮缩放 (scale: 0.95)
- 加载：Spinner 动画
- 成功：Toast 提示
- 失败：错误对话框

---

_简化版完成，核心样式和交互已定义_
