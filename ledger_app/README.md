# 简账APP Android版

## 📱 项目简介

简账APP是一款简洁实用的记账应用，支持Android平台。

## 🛠️ 技术栈

- **Flutter**: 3.16.5
- **Dart**: 3.2.3
- **状态管理**: Provider
- **网络请求**: HTTP
- **本地存储**: SharedPreferences

## 📋 功能清单

### ✅ 已完成
- [x] 项目结构搭建
- [x] 数据模型定义（User, Transaction, Category, Ledger）
- [x] API服务封装
- [x] 登录/注册页面
- [x] 主页框架（账单、统计、我的）

### 🚧 开发中
- [ ] 账单列表展示
- [ ] 添加/编辑账单
- [ ] 数据统计图表
- [ ] 分类管理

### ⏳ 待开发
- [ ] 数据导出
- [ ] 预算管理
- [ ] 个人中心

## 🚀 快速开始

### 环境要求
- Flutter SDK >= 3.16.5
- Android SDK >= API 21
- Android Studio / VS Code

### 安装依赖
```bash
cd ledger_app
flutter pub get
```

### 运行应用
#### 在Android模拟器/真机上运行
```bash
flutter run
```

#### 构建APK
```bash
flutter build apk --release
```

生成的APK文件位于: `build/app/outputs/flutter-apk/app-release.apk`

#### 构建App Bundle (用于Google Play)
```bash
flutter build appbundle --release
```

生成的AAB文件位于: `build/app/outputs/bundle/release/app-release.aab`

## 🔧 配置说明

### 后端API地址
编辑 `lib/services/api_service.dart`:

**开发环境（模拟器）:**
```dart
static const String baseUrl = 'http://10.0.2.2:8080/api/v1';
```

**开发环境（真机）:**
```dart
static const String baseUrl = 'http://YOUR_PC_IP:8080/api/v1';
```

**生产环境:**
```dart
static const String baseUrl = 'https://your-domain.com/api/v1';
```

### Android签名配置

1. 创建keystore文件:
```bash
keytool -genkey -v -keystore ~/keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key
```

2. 配置 `android/key.properties`:
```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=key
storeFile=/path/to/keystore.jks
```

3. 添加到 `.gitignore`:
```
android/key.properties
*.jks
```

## 📁 项目结构

```
lib/
├── main.dart              # 应用入口
├── models/                # 数据模型
│   ├── api_response.dart
│   ├── user.dart
│   ├── transaction.dart
│   ├── category.dart
│   └── ledger.dart
├── services/              # 服务层
│   ├── api_service.dart
│   └── auth_service.dart
├── screens/               # 页面
│   ├── login_screen.dart
│   └── home_screen.dart
├── providers/             # 状态管理
├── widgets/               # 自定义组件
└── utils/                 # 工具类
```

## 🎨 UI预览

### 登录/注册页面
- 手机号输入
- 密码输入
- 登录/注册切换

### 主页面
- 三个标签页：账单、统计、我的
- 底部悬浮按钮添加账单

## 📝 API对接

### 认证API
- `POST /api/v1/users/register` - 用户注册
- `POST /api/v1/users/login` - 用户登录

### 账单API
- `GET /api/v1/transactions` - 获取账单列表
- `POST /api/v1/transactions` - 创建账单
- `PUT /api/v1/transactions/{id}` - 更新账单
- `DELETE /api/v1/transactions/{id}` - 删除账单

### 统计API
- `GET /api/v1/statistics/summary` - 收支统计

## 🔍 故障排查

### 问题：无法连接后端
**解决：**
- 检查后端服务是否启动
- 模拟器使用 `10.0.2.2` 代替 `localhost`
- 真机使用电脑的局域网IP
- 检查防火墙设置

### 问题：构建失败
**解决：**
```bash
flutter clean
flutter pub get
flutter build apk
```

## 📄 开发计划

### Phase 1: 基础功能（当前）
- [x] 登录/注册
- [ ] 账单CRUD
- [ ] 基础统计

### Phase 2: 进阶功能
- [ ] 图表展示
- [ ] 分类管理
- [ ] 数据导出

### Phase 3: 高级功能
- [ ] 预算管理
- [ ] 定时提醒
- [ ] 数据同步

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License
