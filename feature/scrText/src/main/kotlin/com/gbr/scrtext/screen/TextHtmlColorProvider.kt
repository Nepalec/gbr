package com.gbr.scrtext.screen

interface TextHtmlColorProvider {
    fun getSanskritColor(): String
    fun getTranslitColor(): String
    fun getWordByWordColor(): String
    fun getTranslationColor(): String
    fun getCommentColor(): String
    fun getBackgroundColor(): String
    fun getLinkColor(): String
    fun getHighlightColorQuestion(): String
    fun getHighlightColorNote(): String
    fun getHighlightColorHighlight(): String
    fun getSearchColor(): String
    fun getSearchHighlightColor(): String
    fun isDarkMode(): Boolean
}

class DefaultTextHtmlColorProvider : TextHtmlColorProvider {
    override fun getSanskritColor(): String = "#000000"
    override fun getTranslitColor(): String = "#333333"
    override fun getWordByWordColor(): String = "#555555"
    override fun getTranslationColor(): String = "#000000"
    override fun getCommentColor(): String = "#666666"
    override fun getBackgroundColor(): String = "#FFFFFF"
    override fun getLinkColor(): String = "#0066CC"
    override fun getHighlightColorQuestion(): String = "#FFFF00"
    override fun getHighlightColorNote(): String = "#00FF00"
    override fun getHighlightColorHighlight(): String = "#FFD700"
    override fun getSearchColor(): String = "#FF0000"
    override fun getSearchHighlightColor(): String = "#FFE4E1"
    override fun isDarkMode(): Boolean = false
}
