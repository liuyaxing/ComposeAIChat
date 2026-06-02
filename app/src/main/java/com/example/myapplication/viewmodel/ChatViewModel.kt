package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * 聊天 ViewModel
 * - 用户消息 → OkHttp 调本地 Ollama (adb reverse → localhost) → AI 回复
 */
class ChatViewModel : ViewModel() {

    // 配合 `adb reverse tcp:11434 tcp:11434`，模拟器访问 localhost 即宿主机
    private val ollamaUrl = "http://localhost:11434/api/chat"
    private val modelName = "llama3.1:8b"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "你好！我是 AI 助手，有什么可以帮你的吗？",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // 保留上下文用的消息历史（不含 typing 占位）
    private val historyMessages = mutableListOf<JSONObject>()

    init {
        // 把初始问候加入历史
        historyMessages.add(
            JSONObject().apply {
                put("role", "assistant")
                put("content", "你好！我是 AI 助手，有什么可以帮你的吗？")
            }
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 1. 添加用户消息
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true
        )
        historyMessages.add(JSONObject().apply {
            put("role", "user")
            put("content", text)
        })
        _messages.value = _messages.value + userMsg

        // 2. 添加占位的 AI 打字状态
        val typingMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = "",
            isUser = false,
            isTyping = true
        )
        _messages.value = _messages.value + typingMsg
        val typingId = typingMsg.id

        // 3. 调 Ollama
        viewModelScope.launch {
            try {
                val reply = fetchOllamaReply()

                // 逐字打字效果
                var displayedText = ""
                for (ch in reply) {
                    displayedText += ch
                    _messages.value = _messages.value.toMutableList().also { list ->
                        val idx = list.indexOfLast { it.id == typingId }
                        if (idx >= 0) {
                            list[idx] = list[idx].copy(
                                text = displayedText,
                                isTyping = ch != reply.last()
                            )
                        }
                    }
                    delay(30)
                }

                // 更新历史
                historyMessages.add(JSONObject().apply {
                    put("role", "assistant")
                    put("content", reply)
                })

            } catch (e: Exception) {
                // 出错时显示错误提示
                val errorText = when {
                    e.message?.contains("Connection refused") == true ->
                        "⚠️ 连接 Ollama 失败\n\n请确保已在 Windows 上启动 Ollama（系统托盘图标），并运行了模型：\nollama run $modelName"

                    e.message?.contains("timeout") == true ->
                        "⚠️ Ollama 响应超时，模型可能还在加载中，请稍后再试"

                    else -> "⚠️ 出错了：${e.message}"
                }
                _messages.value = _messages.value.toMutableList().also { list ->
                    val idx = list.indexOfLast { it.id == typingId }
                    if (idx >= 0) {
                        list[idx] = list[idx].copy(
                            text = errorText,
                            isTyping = false
                        )
                    }
                }
            }
        }
    }

    /**
     * 调 Ollama API，返回 AI 回复文本
     */
    private suspend fun fetchOllamaReply(): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("model", modelName)
            put("messages", JSONObject.wrap(historyMessages.toList()))
            put("stream", false)
        }

        val request = Request.Builder()
            .url(ollamaUrl)
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("空响应")

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: $responseBody")
        }

        val json = JSONObject(responseBody)
        json.getJSONObject("message").getString("content")
    }
}
