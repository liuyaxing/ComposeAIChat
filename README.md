# 🤖 AI 聊天 Demo — Jetpack Compose

基于 **Jetpack Compose + Material3 + Navigation + ViewModel** 构建的 AI 聊天 Demo，后端接入本地大模型（Ollama）。

> 📌 用于简历项目经历：`探索 Jetpack Compose 声明式UI，在个人项目中落地 Navigation + ViewModel + StateFlow 架构的 AI 聊天 Demo`

---

## 截图

| 欢迎页 | 聊天页 | 中文输入 |
|-------|--------|---------|
| 入口 → 点击"开始聊天" | LazyColumn 消息气泡 + 打字效果 | Trime / Fcitx5 拼音输入 |

---

## 技术栈

| 层 | 技术 |
|---|---|
| **UI** | Jetpack Compose + Material3 |
| **架构** | MVVM（ViewModel + StateFlow） |
| **路由** | Navigation Compose（NavHost） |
| **状态管理** | StateFlow + collectAsState |
| **网络** | OkHttp 4.12.0 |
| **后端** | 本地 Ollama（llama3.1:8b） |
| **输入法** | Trime / Fcitx5（中文拼音支持） |

---

## 功能

-   **欢迎页 → 聊天页**，Navigation 路由完整跳转
-   **消息列表**（LazyColumn），新消息自动滚到底部
-   **用户/AI 气泡区分**，圆角方向自适应
-   **AI 打字效果**（LaunchedEffect 逐字输出 + 闪烁光标）
-   **中文输入**支持（Trime / Fcitx5 拼音均可）
-   **错误处理**（连接失败 / 超时提示）
-   **Material3 主题**适配明暗模式

---

## 项目结构

```
com.example.myapplication/
├── MainActivity.kt         # 入口 + 路由 + 全部 UI
├── model/
│   └── ChatMessage.kt      # 消息数据模型（id, text, isUser, isTyping）
├── viewmodel/
│   └── ChatViewModel.kt    # 状态管理 + OkHttp 请求 Ollama
└── ui/theme/
    ├── Theme.kt            # Material3 主题
    ├── Type.kt             # 字体排版
    └── Color.kt            # 颜色定义
```

---

## 快速开始

### 前置条件

1. **Android Studio**（最新版，自动配置 Compose 依赖）
2. **Ollama** 本地安装并启动（[下载](https://ollama.com)）
3. 拉取模型：

```bash
ollama pull llama3.1:8b
```

### 运行

```bash
# 1. 启动模拟器（x86_64）
# 2. 建立 ADB 反向代理（连接 Windows 上的 Ollama）
adb reverse tcp:11434 tcp:11434

# 3. 在 Android Studio 中运行项目即可
```

> 如使用其他模型，修改 `ChatViewModel.kt` 中的 `MODEL_NAME`。

---

## 主要依赖

| 依赖 | 版本 |
|------|------|
| Compose BOM | 2026.02.01 |
| Navigation Compose | 2.8.6 |
| Lifecycle ViewModel Compose | 2.8.7 |
| OkHttp | 4.12.0 |

---

## 简历关联

该 Demo 对应的简历条目：

> 探索 **Jetpack Compose 声明式UI**，在个人项目中落地 Navigation + ViewModel + StateFlow 架构的 AI 聊天 Demo，验证与现有 MVVM 架构的兼容方案

面试时可展示：
-   如何用 Navigation Compose 替代 Fragment 路由
-   ViewModel + StateFlow 如何管理聊天状态
-   LaunchedEffect 实现打字效果的原理
-   Material3 主题适配
-   中文输入法兼容性处理
