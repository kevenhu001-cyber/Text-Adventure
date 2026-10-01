# 神秘之旅 - 文字冒险游戏

一个基于 Android 的《诡秘之主》同人文字冒险游戏，由 AI 大模型实时驱动叙事。

玩家扮演一名意外卷入超凡世界的霍伊大学历史系学生，通过"占卜家"序列逐步揭开 1349 年贝克兰德的神秘面纱。每次行动由 AI 生成哥特式叙事，并附带结构化的状态标记驱动游戏数值。

## 游戏特性

- **AI 驱动叙事**：接入任意 OpenAI 兼容端点，SSE 流式输出 + 打字机效果
- **多开场剧本**：多个可选开局（含难度分级与彩蛋剧本），从穿越者到外神化身
- **状态系统**：灵性、疯狂值、污染度、三级货币（金镑/苏勒/便士）、背包、状态效果
- **数值反馈**：AI 响应中的 `{spirituality:80/100}` 等标记由 `StateParser` 解析并落到游戏状态；自然语言（"灵性大量消耗"）也能被识别
- **选择分支**：每段叙事附带 2-4 个【选择】，导向不同剧情走向
- **一致性评分**：`StateParser` 对 AI 响应的标记完整性、长度、逻辑冲突打分
- **记忆系统**：`gameMemory` 关键剧情记忆 + 超长对话自动压缩摘要
- **存档系统**：DataStore + Gson JSON 存档，最多 20 个存档位
- **角色详情**：点击状态栏查看属性、装备、关系、命运节点等增强角色面板

## 技术架构

- **语言**：Kotlin（minSdk 26 / targetSdk 34）
- **UI**：Jetpack Compose + Navigation + Material3
- **架构**：MVVM（`GameViewModel` → `GameEngine` → `AiApiService`）
- **网络**：OkHttp（SSE 流式解析），未使用 Retrofit
- **持久化**：DataStore Preferences + Gson（存档与模型配置）

### 核心组件

| 组件 | 职责 |
| --- | --- |
| `GameEngine` | 游戏核心：系统提示词构建、状态更新、冥想/危险判定 |
| `AiApiService` | OpenAI 兼容 API 调用（流式/非流式）、对话历史压缩 |
| `GameViewModel` | UI 状态管理（StateFlow）、流式响应拼装、存档读写 |
| `StateParser` | 解析 AI 响应中的状态标记与自然语言数值变化 |
| `ChoiceParser` | 解析【选择】分支选项 |
| `SaveRepository` | 存档的保存/加载/删除（JSON 存储） |

### 状态标记协议

AI 响应需包含以下格式标记：

```
{spirituality:80/100}   灵性值（当前/最大）
{madness:15}            疯狂值
{money:15/12/6}         金钱（金镑/苏勒/便士）
{location:贝克兰德广场}   当前位置
{time:1349年11月3日 上午10点}
{inventory:+神秘钥匙,-学生证}
{status:+疲惫,-中毒}
{memory:得知了灰雾之上的秘密}
```

## 配置 AI 模型

**密钥不在代码或仓库中保存**，全部在应用内配置：

1. 打开应用 → 进入 **设置**（游戏页右上角）
2. 点击 **添加自定义模型**，填写：
   - **API URL**：OpenAI 兼容的 Chat Completions 地址，例如
     `https://api.openai.com/v1/chat/completions`
     `https://api.siliconflow.cn/v1/chat/completions`
     `https://api.deepseek.com/chat/completions`
   - **Model ID**：如 `gpt-4o-mini`、`deepseek-chat`
   - **API Key**：你的密钥
3. 可点击 **测试连接** 验证配置，保存后在列表中点选启用
4. 游戏页底部的模型按钮可随时切换已保存的模型

> 提示：`usesCleartextTraffic` 已开启，支持局域网/本地 HTTP 端点（如 Ollama `http://<主机IP>:11434/v1/chat/completions`）。

## 构建和运行

1. 安装 Android SDK（`local.properties` 中的 `sdk.dir` 指向本机 SDK）
2. 在 Android Studio 中打开项目，同步 Gradle
3. 连接设备或启动模拟器，运行 `app`
4. 命令行构建：`gradlew.bat :app:assembleDebug`
5. 运行单元测试：`gradlew.bat :app:testDebugUnitTest`

## 故障排除

- **提示"尚未配置 AI 模型"**：先去设置页添加并启用一个模型
- **API 调用失败**：在编辑模型对话框里点"测试连接"查看具体错误（密钥无效、URL 错误、额度不足等）
- **流式输出中断**：检查网络；日志中 `解析流式数据失败` 表示个别 SSE 分片被跳过，不影响整体
