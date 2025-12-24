package com.gbr.datasource.notes.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_tags",
    foreignKeys = [
        ForeignKey(
            entity = TextNoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["noteId"]),
        Index(value = ["tagId"]),
        Index(value = ["noteId", "tagId"], unique = true)
    ]
)
data class NoteTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int,
    val tagId: Int
)
