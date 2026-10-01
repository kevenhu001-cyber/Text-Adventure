# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# 保留注解与泛型签名：Gson 依赖反射读取字段，签名信息一旦被裁掉反序列化会直接失败
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep 数据模型
# 注意：包名是 mysteriousjourney，早期这里误写成 mysterioujourney，导致规则从未匹配到任何类，
# 一旦开启混淆，GameState/PlayerState 会被裁剪，存档文件随之失效。
-keep class com.mysteriousjourney.domain.model.** { *; }
-keep class com.mysteriousjourney.data.model.** { *; }
-keep class com.mysteriousjourney.data.settings.** { *; }
-keep class com.mysteriousjourney.GameApplication { *; }

# ViewModel / Compose 通过反射与约定实例化，保留构造签名
-keep class com.mysteriousjourney.ui.GameViewModel { <init>(...); }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# org.json 在 Android 上属于平台 API，无需额外 keep
-dontwarn org.json.**

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
