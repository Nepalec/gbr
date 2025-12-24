package com.gbr.datasource.notes.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gbr.datasource.notes.converters.NotesTypeConverters

@Entity(tableName = "text_notes")
@TypeConverters(NotesTypeConverters::class)
data class TextNoteEntity(
    @PrimaryKey
    val id: Int,
    val gb: String, // GitabaseID stored as String (key)
    val bookId: Int?,
    val bookCode: String?,
    val chapter: Int?,
    val textNo: String,
    val textId: String,
    val type: Int, // NoteType stored as Int
    val place: Int, // NotePlace stored as Int
    val selectedText: String?,
    val selectedTextStart: Int?,
    val selectedTextEnd: Int?,
    val selectedTextScrollPos: Int?,
    val userComment: String?,
    val userSubject: String?,
    val dateCreated: Int?,
    val dateModified: Int?
)
