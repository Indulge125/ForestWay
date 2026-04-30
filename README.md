<div align="center">
  <h1>🌿 森途 (ForestWay)</h1>
  <p><strong>基于行为激励的低碳出行数字平台 | A Gamified Low-Carbon Travel Ecosystem</strong></p>
  <p>
    <a href="https://developer.android.com/kotlin"><img src="https://img.shields.io/badge/Kotlin-1.9.0-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"></a>
    <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack_Compose-1.5.0-4285F4.svg?logo=android&logoColor=white" alt="Jetpack Compose"></a>
    <a href="https://github.com/Indulge125/ForestWay/blob/master/LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License"></a>
    <a href="#"><img src="https://img.shields.io/badge/AI_Assisted-Gemini_3_Pro-00E5FF.svg?logo=googlegemini&logoColor=white" alt="Gemini 3 Pro"></a>
  </p>
</div>

---

## 📖 项目简介 (Introduction)

**森途 (ForestWay)** 是一款专为倡导绿色生活方式而设计的 Android 原生应用。本项目不仅是一个高精度的轨迹记录工具，更是一个将枯燥的环保行为**游戏化、社交化**的数字平台。

本项目是 **2026年中国大学生计算机设计大赛（软件应用与开发赛道）** 的参赛作品，核心架构与代码在 **Gemini 3 Pro** 的深度协助下完成，展现了 AI 与现代移动端开发技术（Modern Android Development）的完美结合。

> 💡 **核心理念**：让每一次对地球的告白都清晰可见。

---

## ✨ 核心功能 (Core Features)

- 🗺️ **高精度轨迹追踪**：深度集成 **百度地图 SDK**，结合 **WGS84 椭球体算法**，精准记录步行、骑行等移动轨迹与里程。
- ♻️ **科学碳排计算**：根据不同出行模式的碳排放系数，科学量化用户的环保贡献（碳减排量），实测数据已精准对标市面成熟运动软件（如 Keep）。
- 🎮 **游戏化激励体系 (Gamification)**：
  - **虚拟苗圃**：用户可消耗碳积分“种植”专属虚拟树木。
  - **成长等级**：10 级用户成长体系（从“新手守护者”到“碳中和传奇”）。
  - **成就勋章**：丰富的里程碑触发式勋章收集。
- 🎨 **社交分享引擎**：自研底层 Canvas 多层绘图引擎，一键将地图轨迹、个性化数据合成为精美的环保海报。
- 🔐 **多用户数据隔离**：基于 Room 数据库构建完善的本地账户系统，支持单设备多账号独立运行。

---

## 🛠️ 技术架构 (Tech Stack)

本项目严格遵循 Google 推荐的 **MAD (Modern Android Development)** 最佳实践：

- **编程语言**: 100% Kotlin
- **UI 框架**: 全面拥抱 **Jetpack Compose** 声明式 UI
- **架构模式**: 单向数据流 (UDF) 的 **MVVM** 架构
- **异步处理**: Kotlin Coroutines & StateFlow
- **依赖注入**: Dagger Hilt
- **本地存储**: Room Database (SQLite)
- **图像加载**: Coil
- **核心服务**: 前台服务保活机制 (Foreground Service) 处理断点续传

> 📊 **架构详情**：请查看项目目录下的 [`architecture_diagram.md`](architecture_diagram.md) 获取企业级系统逻辑框图。

---

## 🚀 快速开始 (Getting Started)

### 1. 环境要求
- Android Studio Iguana (2023.2.1) 或更高版本
- JDK 17
- 最低支持 Android API Level 26 (Android 8.0)

### 2. 克隆与运行
```bash
# 克隆仓库
git clone https://github.com/Indulge125/ForestWay.git

# 使用 Android Studio 打开项目
# 等待 Gradle 同步完成后，点击 Run 即可在真机或模拟器上运行
```

### 3. 关于百度地图 AK (重要)
为了保证地图功能正常显示，你需要在百度地图开放平台申请自己的 `API_KEY`，并替换 `AndroidManifest.xml` 中的默认值，或者在 `local.properties` 中配置（推荐）。

---

## 🤖 AI 研发赋能 (AI-Driven Development)

本项目的成功落地深度受益于大模型时代的基础设施：
1. **架构与重构**：核心业务逻辑与 Compose 视图的解耦由 **Gemini 3 Pro** 提供长链推理与代码重构建议。
2. **底层调优**：在遇到复杂的前台保活服务、Canvas 渲染性能瓶颈时，通过构建 AI 闭环工作流实现了高效的结构化调试。

---

## 📄 开源协议 (License)

本项目基于 [Apache License 2.0](LICENSE) 协议开源。欢迎 Fork、提交 PR 或将其作为你学习 Jetpack Compose 的参考案例。
