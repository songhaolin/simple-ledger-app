# 记账页面 UI 设计

> _简账 - 记账功能界面_
> _创建者：菜菜子 🤖_

---

## 📋 页面概览

记账页面是核心功能，要求**3步完成**：
1. 输入金额
2. 选择分类
3. 点击保存

---

## 🎯 布局结构

```
┌─────────────────────────────────┐
│  ← 记一笔              完成   │  导航栏 (56px)
├─────────────────────────────────┤
│                                 │
│  收入   支出   转账          │  类型切换 (48px)
│   ✓                           │  ✓表示当前选中
├─────────────────────────────────┤
│                                 │
│         ¥ 35.00               │  金额输入 (100px)
│                                 │
├─────────────────────────────────┤
│  分类                  >       │  分类选择 (48px)
├─────────────────────────────────┤
│                                 │
│  ┌──┬──┬──┬──┬──┬──┐      │  分类网格 (8列)
│  │🍚│🚗│🛍️│🎮│🏠│💊│      │
│  ├──┴──┴──┴──┴──┴──┤      │
│  │餐饮│交通│购物│娱乐│      │  分类名称
│  └───┬──┴──┬┴──┬──┘      │
│      │    │    │        │
│  ┌──┴──┐┌──┴──┐┌──┴──┐    │
│  │📚   ││💊   ││📦   │    │
│  │教育  ││医疗  ││其他  │    │
│  └───┬──┘└───┬──┘└───┬──┘    │
└──────┴────────┴───────┴──────┘
│                                 │
├─────────────────────────────────┤
│  备注                          │  备注输入 (48px)
│  ┌───────────────────────────┐ │
│  │                           │ │
│  └───────────────────────────┘ │
│                                 │
├─────────────────────────────────┤
│  日期：今天  >               │  日期选择 (48px)
│                                 │
│  图片：📷 + 上传凭证          │  图片上传 (48px)
│  ┌─────┬─────┬─────┐        │
│  │ 📷  │ 📷  │ 📷  │        │  已上传图片
│  └─────┴─────┴─────┘        │
│                                 │
├─────────────────────────────────┤
│         保存                    │  保存按钮 (56px)
└─────────────────────────────────┘
```

---

## 💡 样式定义

### 金额输入

```css
.amount-input-container {
  padding: 32px 16px;
  text-align: center;
  background: #FFFFFF;
}

.amount-input {
  font-size: 48px;
  font-weight: 600;
  color: #2D3436;
  text-align: center;
  border: none;
  outline: none;
}

.currency-symbol {
  font-size: 24px;
  color: #B2BEC3;
  margin-right: 4px;
}
```

---

### 类型切换

```css
.type-switcher {
  display: flex;
  padding: 12px 16px;
  background: #F5F6FA;
  border-radius: 8px;
  margin: 0 16px;
}

.type-option {
  flex: 1;
  text-align: center;
  padding: 12px;
  font-size: 16px;
  color: #636E72;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.type-option.active {
  background: #4ECDC4;
  color: #FFFFFF;
  font-weight: 500;
}

.type-option.expense.active {
  background: #FF6B6B;
}

.type-option.income.active {
  background: #10AC84;
}
```

---

### 分类网格

```css
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px;
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

.category-item:hover {
  background: #E8ECF1;
}

.category-item.selected {
  background: #4ECDC4;
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3);
}

.category-item.expense.selected {
  background: #FF6B6B;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 12px;
  color: #636E72;
}

.category-item.selected .category-name {
  color: #FFFFFF;
}
```

---

### 备注输入

```css
.note-input-container {
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
  min-height: 80px;
  padding: 12px;
  font-size: 16px;
  color: #2D3436;
  background: #F5F6FA;
  border: none;
  border-radius: 12px;
  resize: none;
  outline: none;
}

.note-input::placeholder {
  color: #B2BEC3;
}
```

---

### 日期选择

```css
.date-picker-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #FFFFFF;
  border-top: 1px solid #F5F6FA;
}

.date-label {
  font-size: 14px;
  color: #636E72;
}

.date-value {
  font-size: 16px;
  color: #2D3436;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.date-icon {
  margin-left: 4px;
  font-size: 14px;
  color: #B2BEC3;
}
```

---

### 图片上传

```css
.image-upload-container {
  padding: 12px 16px;
  background: #FFFFFF;
  border-top: 1px solid #F5F6FA;
}

.upload-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #636E72;
  margin-bottom: 12px;
}

.upload-button {
  width: 80px;
  height: 80px;
  border: 2px dashed #DFE4EA;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-button:hover {
  border-color: #4ECDC4;
  background: #F5F6FA;
}

.upload-icon {
  font-size: 32px;
  color: #B2BEC3;
  margin-bottom: 4px;
}

.upload-text {
  font-size: 10px;
  color: #B2BEC3;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 12px;
}

.image-preview {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-preview .delete {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  cursor: pointer;
  font-size: 16px;
}
```

---

### 保存按钮

```css
.save-button-container {
  padding: 16px;
  background: #FFFFFF;
  border-top: 1px solid #F5F6FA;
}

.save-button {
  width: 100%;
  height: 48px;
  background: #4ECDC4;
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.save-button:hover {
  background: #3BAEAC;
  transform: translateY(-1px);
}

.save-button:active {
  transform: translateY(0);
}

.save-button:disabled {
  background: #DFE4EA;
  color: #B2BEC3;
  cursor: not-allowed;
}

.save-button.expense {
  background: #FF6B6B;
}

.save-button.income {
  background: #10AC84;
}
```

---

## 🔄 交互流程

### 进入记账页面

```
点击首页浮动按钮
  ↓
进入记账页面
  ↓
默认状态：
  - 类型：支出
  - 金额：0.00
  - 分类：无
  - 日期：今天
  - 备注：空
```

---

### 输入金额

```
点击金额输入框
  ↓
弹出数字键盘
  ↓
输入数字
  ↓
实时显示：¥XX.XX
  ↓
焦点在最后一位
```

---

### 选择分类

```
点击分类图标
  ↓
分类高亮（background变色 + 阴影）
  ↓
保存分类ID到状态
```

---

### 添加备注

```
点击备注输入框
  ↓
展开输入框（min-height: 80px）
  ↓
输入文字
  ↓
实时更新备注状态
```

---

### 选择日期

```
点击日期
  ↓
弹出日期选择器
  ↓
选择日期
  ↓
更新日期显示
```

---

### 上传图片

```
点击上传按钮
  ↓
打开相册/相机
  ↓
选择图片
  ↓
显示预览图
  ↓
支持多图（最多3张）
  ↓
点击删除图标删除图片
```

---

### 保存记账

```
点击保存按钮
  ↓
验证：
  - 金额 > 0 ✓
  - 已选择分类 ✓
  ↓
显示加载动画
  ↓
提交数据到后端
  ↓
成功：
  - 返回首页
  - 刷新账单列表
  - 显示成功提示

失败：
  - 显示错误提示
  - 保持在记账页面
```

---

## 📝 验证规则

| 字段 | 规则 | 错误提示 |
|------|------|---------|
| 金额 | 必须 > 0 | 请输入金额 |
| 分类 | 必须选择 | 请选择分类 |
| 备注 | 可选，最多100字 | 备注不能超过100字 |
| 图片 | 可选，最多3张 | 最多上传3张图片 |

---

## ⚡ 性能优化

1. **分类图标缓存**：使用 CachedNetworkImage
2. **图片压缩**：上传前压缩到 500KB 以下
3. **防抖**：备注输入 500ms 防抖
4. **懒加载**：分类图标按需加载

---

_记账页面设计完成_
