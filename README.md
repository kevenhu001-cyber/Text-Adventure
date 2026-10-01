# 神秘之旅 - 文字冒险游戏

一个基于Android的克苏鲁风格文字冒险游戏，使用AI驱动的故事叙述。

## 问题修复

### 已修复的问题：
1. ✅ **API密钥配置** - 添加了灵活的API密钥配置方式
2. ✅ **GameViewModel集成** - 修复了GameEngine的正确使用
3. ✅ **系统提示词格式** - 修复了格式错误
4. ✅ **故事推进逻辑** - 现在可以正确处理玩家输入并生成AI响应

## 配置说明

### 1. API密钥配置

**方式一：环境变量（推荐）**
```bash
export OPENAI_API_KEY=your_actual_api_key_here
```

**方式二：配置文件**
1. 编辑项目根目录下的 `api.properties` 文件
2. 将 `your_openai_api_key_here` 替换为你的实际OpenAI API密钥：
```properties
OPENAI_API_KEY=sk-your-actual-api-key-here
```

### 2. 获取OpenAI API密钥
1. 访问 [OpenAI官网](https://platform.openai.com/)
2. 注册/登录账户
3. 在API Keys页面创建新的API密钥
4. 复制密钥并按照上述方式配置

## 游戏特性

- **AI驱动叙事**：基于GPT-3.5的动态故事生成
- **状态系统**：灵性、疯狂值、金钱等RPG元素
- **克苏鲁风格**：维多利亚时代的哥特式恐怖氛围
- **选择影响**：玩家的每个选择都会影响后续剧情

## 技术架构

- **语言**：Kotlin
- **框架**：Android + Jetpack Compose
- **架构**：MVVM + Clean Architecture
- **AI集成**：OpenAI GPT-3.5 API
- **本地存储**：Room数据库

## 构建和运行

1. **配置API密钥**（必须先完成此步骤）
2. 在Android Studio中打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 点击运行按钮

## 故事背景

游戏设定在1349年的贝克兰德（伦敦），玩家扮演一名霍伊大学的历史系学生，意外卷入了超凡力量的世界。通过"占卜家"序列，玩家将逐步揭开这个世界的神秘面纱...

## 故障排除

### 问题：故事卡在背景介绍
**解决方案**：
1. 确认API密钥已正确配置
2. 检查网络连接
3. 查看Logcat中的错误信息
4. 确认OpenAI账户有足够的API额度

### 问题：API调用失败
**可能原因**：
- API密钥无效或过期
- 网络连接问题
- OpenAI服务暂时不可用
- API额度不足

## 开发说明

### 核心组件
- `GameEngine`: 游戏核心逻辑
- `AiApiService`: AI API交互
- `GameViewModel`: UI状态管理
- `StateParser`: AI响应解析

### 状态格式
AI响应需要包含特定格式的状态标记：
```
{spirituality:80/100}
{madness:15}
{money:15/12/6}
{location:贝克兰德广场}
{time:1349年11月3日 上午10点}
{inventory:+神秘钥匙,-学生证}
{status:+疲惫}
```
