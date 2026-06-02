# 🤖 AI 聊天 Demo — Jetpack Compose

基于 Jetpack Compose + Material3 + Navigation + ViewModel 构建的 AI 聊天 Demo，后端接入本地大模型（Ollama）。

## 技术栈

- **UI**: Jetpack Compose + Material3
- **架构**: MVVM (ViewModel + StateFlow)
- **路由**: Navigation Compose
- **网络**: OkHttp + Ollama API
- **后端**: 本地 Ollama (llama3.1:8b)

## 功能

- 欢迎页 → 聊天页，Navigation 路由
- 消息列表（LazyColumn），自动滚到底部
- 用户消息 / AI 回复气泡区分
- 打字效果（LaunchedEffect 逐字输出）
- 中文输入支持（Fcitx5 / Trime 输入法均可）
- AI 回复错误提示（连接失败、超时等）

## 项目结构

```
com.example.myapplication/
├── MainActivity.kt         # 入口 + 路由 + UI
├── model/
│   └── ChatMessage.kt      # 消息数据模型
├── viewmodel/
│   └── ChatViewModel.kt    # 状态管理 + 网络请求
└── ui/theme/
    ├── Theme.kt
    ├── Type.kt
    └── Color.kt
```

## 运行要求

- Android Studio (最新版)
- minSdk 24, targetSdk 36
- Android 模拟器 (x86_64) 或真机
- **Ollama 本地服务**（见下方说明）

### 连接 Ollama（模拟器）

默认地址 `http://localhost:11434`，模拟器需执行：

```bash
adb reverse tcp:11434 tcp:11434
```

模型：`llama3.1:8b`（可在 `ChatViewModel.kt` 中修改 `MODEL_NAME`）

## 主要依赖

| 依赖 | 版本 |
|------|------|
| Compose BOM | 2026.02.01 |
| Navigation Compose | 2.8.6 |
| Lifecycle ViewModel Compose | 2.8.7 |
| OkHttp | 4.12.0 |
