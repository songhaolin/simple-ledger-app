# 简账 (Simple Ledger)

一个简单易用的个人和家庭记账应用，支持 Android 和 iOS。

## 📋 项目概览

**产品名称**：简账
**定位**：简化版鲨鱼记账
**目标**：让记账变得简单、智能、有趣

## 🎯 核心功能

### V1.0 MVP
- ✅ 个人账本管理
- ✅ 家庭账本（多人协作）
- ✅ 快速记账（3步完成）
- ✅ 分类管理（预设+自定义）
- ✅ 账单列表展示
- ✅ 基础统计分析
- ✅ 预算管理
- ✅ 数据云同步

### V1.1 增强
- 高级统计分析
- 报表导出（Excel/PDF）
- 记账提醒

### V2.0 未来
- 智能记账（语音/拍照识别）
- 社区功能
- 多账本管理

## 🛠️ 技术栈

### 后端
- **语言**：Java 17
- **框架**：Spring Boot 3.2
- **数据库**：MongoDB 7.0
- **缓存**：Redis 7.2
- **认证**：JWT

### 前端
- **框架**：Flutter 3.16
- **语言**：Dart 3.2
- **状态管理**：Riverpod 2.4
- **本地存储**：Hive 2.2
- **网络**：Dio 5.4
- **图表**：FL Chart 0.65

### 部署
- **容器**：Docker
- **反向代理**：Nginx
- **CI/CD**：GitHub Actions

## 📁 项目结构

```
ledger-app/
├── docs/                    # 项目文档
│   ├── 01-PRD.md           # 产品需求文档
│   ├── 02-TECHNICAL.md     # 技术方案
│   ├── 03-UI-DESIGN.md     # UI 设计文档
│   └── 04-API.md           # API 文档
├── backend/                 # 后端代码
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/                # 前端代码
│   ├── lib/
│   │   ├── main.dart
│   │   ├── models/
│   │   ├── screens/
│   │   ├── widgets/
│   │   └── services/
│   ├── android/
│   ├── ios/
│   └── pubspec.yaml
├── scripts/                 # 部署脚本
│   ├── deploy.sh
│   └── setup.sh
├── docker-compose.yml
└── README.md
```

## 🚀 快速开始

### 后端开发

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
flutter pub get
flutter run
```

### 部署

```bash
docker-compose up -d
```

## 📅 开发计划

- Week 1: 需求确认
- Week 2-4: 后端开发
- Week 5-8: 前端开发
- Week 9-10: 测试与优化
- Week 11: 部署上线

详细计划见：[产品需求文档](./docs/01-PRD.md)

## 📞 联系方式

- 产品负责人：宋宋
- 技术负责人：菜菜子 🤖

## 📄 许可证

Copyright © 2026

---

_由菜菜子创建和管理 🤖_
