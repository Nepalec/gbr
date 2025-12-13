package com.gbr.model.notes

/**
 * Доменная модель заметки с тегами.
 * Используется для отображения заметок на экранах книги/главы/текста.
 * Автоматически обновляется при изменении заметки, тегов или связей noteTags.
 */
data class TextNoteWithTags(
    val note: TextNote,
    val tags: List<Tag>
) {
    /**
     * Проверяет, имеет ли заметка хотя бы один тег
     */
    val hasTags: Boolean
        get() = tags.isNotEmpty()
    
    /**
     * Возвращает список имен тегов
     */
    val tagNames: List<String>
        get() = tags.map { it.name }
    
    /**
     * Проверяет, имеет ли заметка тег с указанным ID
     */
    fun hasTag(tagId: Int): Boolean {
        return tags.any { it.id == tagId }
    }
    
    /**
     * Проверяет, имеет ли заметка тег с указанным именем
     */
    fun hasTag(tagName: String): Boolean {
        return tags.any { it.name == tagName }
    }
}

