# Android App构建指南

## 📋 构建前准备

### 1. 检查Flutter环境
```bash
flutter doctor -v
```

确保以下项目都打勾：
- Flutter SDK
- Android toolchain
- Android Studio
- Connected device (模拟器或真机)

### 2. 检查依赖
```bash
cd /home/ubuntu/projects/ledger-app/ledger_app
flutter pub get
```

## 🚀 构建步骤

### 方式一：在模拟器/真机上运行

#### 启动模拟器
```bash
flutter emulators
flutter emulators --launch <emulator_id>
```

#### 运行应用
```bash
flutter run
```

### 方式二：构建APK文件

#### Debug版本（用于测试）
```bash
flutter build apk --debug
```
输出: `build/app/outputs/flutter-apk/app-debug.apk`

#### Release版本（用于发布）
```bash
flutter build apk --release
```
输出: `build/app/outputs/flutter-apk/app-release.apk`

### 方式三：构建App Bundle（用于Google Play）

```bash
flutter build appbundle --release
```
输出: `build/app/outputs/bundle/release/app-release.aab`

## 🔐 配置签名（发布用）

### 1. 创建密钥库
```bash
keytool -genkey -v -keystore ~/keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key
```

### 2. 创建配置文件
创建 `android/key.properties`:
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=key
storeFile=/home/ubuntu/keystore.jks
```

### 3. 修改build.gradle

在 `android/app/build.gradle` 中添加:

```groovy
// 在文件开头添加
def keystoreProperties = new Properties()
def keystorePropertiesFile = rootProject.file('key.properties')
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    // ... 其他配置

    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile keystoreProperties['storeFile'] ? file(keystoreProperties['storeFile']) : null
            storePassword keystoreProperties['storePassword']
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

## 📱 安装APK到设备

### 通过ADB安装
```bash
adb install build/app/outputs/flutter-apk/app-release.apk
```

### 通过USB传输
直接复制APK文件到手机，使用文件管理器安装

## 🔧 常见问题

### Q: 构建失败，提示gradle错误
A: 运行 `flutter clean` 然后重新构建

### Q: 网络请求失败
A: 检查API地址配置，确保后端服务正在运行

### Q: 模拟器无法启动
A: 检查Android Studio的AVD Manager中是否有可用的模拟器

## 📊 构建产物说明

| 文件 | 用途 | 大小 |
|------|------|------|
| app-debug.apk | 调试测试 | ~50MB |
| app-release.apk | 正式发布 | ~20MB |
| app-release.aab | Google Play发布 | ~15MB |

## 🎯 发布到应用市场

### Google Play
1. 构建AAB文件
2. 在Google Play Console创建应用
3. 上传AAB文件
4. 填写应用信息
5. 提交审核

### 第三方市场
1. 构建APK文件
2. 签名（使用上述方法）
3. 上传到对应市场

## 📝 版本号配置

编辑 `pubspec.yaml`:
```yaml
version: 1.0.0+1  # 1.0.0是版本名，+1是版本号
```

每次发布时，增加版本号:
- 1.0.0+1 → 1.0.0+2 (小更新)
- 1.0.0+1 → 1.0.1+2 (功能更新)
- 1.0.0+1 → 1.1.0+2 (重要更新)
- 1.0.0+1 → 2.0.0+2 (重大更新)
