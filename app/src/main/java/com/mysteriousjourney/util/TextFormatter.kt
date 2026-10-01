package com.mysteriousjourney.util

object TextFormatter {
    
    fun formatText(text: String): String {
        var result = text
        
        result = result.replace(Regex("\n{3,}"), "\n\n")
        
        result = result.replace(Regex("^[ \\t]+", RegexOption.MULTILINE), "")
        
        result = result.replace(Regex("[ \\t]+$", RegexOption.MULTILINE), "")
        
        result = result.trim()
        
        val lines = result.split("\n")
        val formattedLines = mutableListOf<String>()
        var prevEmpty = false
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            if (trimmedLine.isEmpty()) {
                if (!prevEmpty) {
                    formattedLines.add("")
                    prevEmpty = true
                }
            } else {
                formattedLines.add(trimmedLine)
                prevEmpty = false
            }
        }
        
        return formattedLines.joinToString("\n")
    }
    
    fun cleanNarrativeText(text: String): String {
        var result = text
        
        result = result.replace(Regex("\\{[^}]+\\}"), "")
        
        result = formatText(result)
        
        return result
    }
}
