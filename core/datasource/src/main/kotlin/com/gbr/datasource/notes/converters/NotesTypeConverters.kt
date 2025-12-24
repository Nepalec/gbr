package com.gbr.datasource.notes.converters

import androidx.room.TypeConverter
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NotePlace
import com.gbr.model.notes.NoteType

object NotesTypeConverters {
    // GitabaseID converters
    @TypeConverter
    fun fromGitabaseID(gitabaseID: GitabaseID): String {
        return gitabaseID.key
    }

    @TypeConverter
    fun toGitabaseID(key: String): GitabaseID {
        return GitabaseID(key)
    }

    // NoteType converters
    @TypeConverter
    fun fromNoteType(noteType: NoteType): Int {
        return noteType.value
    }

    @TypeConverter
    fun toNoteType(value: Int): NoteType {
        return NoteType.entries.firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown NoteType value: $value")
    }

    // NotePlace converters
    @TypeConverter
    fun fromNotePlace(notePlace: NotePlace): Int {
        return notePlace.value
    }

    @TypeConverter
    fun toNotePlace(value: Int): NotePlace {
        return NotePlace.entries.firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown NotePlace value: $value")
    }
}
