# 统计页面 UI 设计

> _简账 - 统计分析界面_
> _创建者：菜菜子 🤖_

---

## 📋 页面概览

统计页面展示收支数据可视化，支持**日/周/月/年**切换。

---

## 🎯 布局结构

```
┌─────────────────────────────────┐
│  统计                          │  导航栏 (56px)
├─────────────────────────────────┤
│  日  周  月  年              │  时间切换 (48px)
│   ✓                           │  ✓当前选中
├─────────────────────────────────┤
│                                 │
│  ┌──────────────────────┐     │  收支卡片 (100px)
│  │ 本月收支             │     │
│  │ ──────────────────   │     │
│  │ 收入：¥5,678.90    │     │
│  │ 支出：¥3,456.78    │     │
│  │ 结余：¥2,222.12    │     │
│  └──────────────────────┘     │
│                                 │
│  ┌──────────────────────┐     │  饼图 (200px)
│  │                      │     │  分类占比
│  │        ●●●          │     │
│  │      ●●●●●         │     │
│  │    ●●●●●●●●       │     │
│  │                      │     │
│  │ 餐饮  30%           │     │
│  │ 交通  20%           │     │
│  │ 购物  15%          │     │
│  └──────────────────────┘     │
│                                 │
│  ───── 分类排行 ────         │  分组标题
│                                 │
│  1. 🍚 餐饮   ¥1,037.03 30%  │  排行项 (48px)
│  ━━━━━━━━━━━━━━━━━━ 30%   │  进度条
│                                 │
│  2. 🚗 交通   ¥691.36   20%  │
│  ━━━━━━━━━━━━━          20%   │
│                                 │
│  3. 🛍️ 购物   ¥518.52   15%  │
│  ━━━━━━━━━━━           15%   │
│                                 │
│  4. 🎮 娱乐   ¥345.68   10%  │
│  ━━━━━━               10%   │
│                                 │
├─────────────────────────────────┤
│  📒账本  📊统计  🎯预算  👤我的  │  底部Tab (56px)
└─────────────────────────────────┘
```

---

## 💡 样式定义

### 收支卡片

```css
.summary-card {
  padding: 16px;
  margin: 16px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.summary-title {
  font-size: 16px;
  font-weight: 500;
  color: #636E72;
  margin-bottom: 12px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #F5F6FA;
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-label {
  font-size: 14px;
  color: #636E72;
}

.summary-amount {
  font-size: 18px;
  font-weight: 600;
}

.summary-amount.income {
  color: #10AC84;
}

.summary-amount.expense {
  color: #FF6B6B;
}

.summary-amount.balance {
  color: #2D3436;
}
```

---

### 饼图

```css
.chart-container {
  padding: 16px;
  background: #FFFFFF;
  border-radius: 16px;
  margin: 0 16px 16px;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
  padding: 12px;
  background: #F5F6FA;
  border-radius: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #636E72;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  margin-right: 4px;
}
```

---

### 分类排行

```css
.ranking-container {
  padding: 0 16px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  background: #FFFFFF;
  border-radius: 12px;
  margin-bottom: 8px;
  padding: 12px;
}

.ranking-icon {
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

.ranking-info {
  flex: 1;
}

.ranking-name {
  font-size: 14px;
  color: #2D3436;
  margin-bottom: 4px;
}

.ranking-amount {
  font-size: 16px;
  font-weight: 600;
  color: #2D3436;
}

.ranking-percentage {
  margin-left: auto;
  font-size: 14px;
  color: #636E72;
}

.progress-bar {
  height: 4px;
  background: #F5F6FA;
  border-radius: 2px;
  margin-top: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s;
}
```

---

## 🔄 交互流程

### 时间切换

```
点击 日/周/月/年
  ↓
高亮当前选项
  ↓
加载对应时间段数据
  ↓
刷新图表和列表
```

---

### 点击饼图

```
点击饼图扇区
  ↓
高亮对应分类
  ↓
显示详细数据
```

---

### 点击排行项

```
点击分类排行项
  ↓
跳转到该分类详情页
  ↓
显示该分类所有账单
```

---

## 📊 时间维度设计

### 日统计

- 今日收支总额
- 今日消费排行
- 今日消费趋势（24小时）

### 周统计

- 本周收支总额
- 每日消费趋势（7天）
- 分类占比

### 月统计

- 本月收支总额
- 每日消费趋势（30天）
- 分类占比
- 同比上月

### 年统计

- 本年收支总额
- 月度消费趋势（12个月）
- 季度分析
- 同比去年

---

_统计页面设计完成_
