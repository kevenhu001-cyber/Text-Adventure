import com.mysteriousjourney.util.StateParser

/**
 * 测试优化效果的示例文件
 * 用于验证文本格式优化、状态识别改进和叙事吸引力增强的效果
 */
fun main() {
    // 模拟AI生成的原始文本（优化前）
    val originalText = "你站在贝克兰德广场的阴影里，阴冷的雾气包裹着你，远处传来教堂的钟声，你感到一阵灵性的消耗，你看到一个穿着黑色风衣的男子向你走来，他的脸上带着神秘的微笑，你感到一阵不安，你决定是否要和他交谈？{location:贝克兰德广场} {time:1349年11月3日 上午10点} {spirituality:90/100} {madness:5}"
    
    println("=== 优化前效果 ===")
    println(originalText)
    println()
    
    println("=== 优化后效果 ===")
    val parseResult = StateParser.parseResponse(originalText)
    println("叙事文本：")
    println(parseResult.narrative)
    println()
    println("状态更新：")
    println(parseResult.stateUpdate)
    println()
    println("一致性评分：")
    println(parseResult.consistencyScore)
    println()
    
    // 测试更复杂的文本
    val complexText = "夜幕降临，贝克兰德的街道被黑暗笼罩，你走在潮湿的石板路上，脚步声在巷子里回荡。突然，你听到身后传来急促的脚步声，你转身看到一个穿着破烂衣服的男孩正拼命向你跑来，"救命！有人在追我！"他气喘吁吁地说道。你感到一阵灵性的波动，似乎有什么不祥的事情即将发生。你决定是否要帮助这个男孩？{location:贝克兰德东区小巷} {time:1349年11月3日 晚上8点} {spirituality:85/100} {madness:8} {inventory:+生锈的钥匙}"
    
    println("=== 复杂文本优化后效果 ===")
    val complexResult = StateParser.parseResponse(complexText)
    println("叙事文本：")
    println(complexResult.narrative)
    println()
    println("状态更新：")
    println(complexResult.stateUpdate)
    println()
    println("一致性评分：")
    println(complexResult.consistencyScore)
}
