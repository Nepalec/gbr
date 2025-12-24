package com.gbr.datasource.notes

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gbr.datasource.notes.converters.NotesTypeConverters
import com.gbr.datasource.notes.dao.NoteTagDao
import com.gbr.datasource.notes.dao.ReadingDao
import com.gbr.datasource.notes.dao.TagDao
import com.gbr.datasource.notes.dao.TextNoteDao
import com.gbr.datasource.notes.entity.NoteTagEntity
import com.gbr.datasource.notes.entity.ReadingEntity
import com.gbr.datasource.notes.entity.TagEntity
import com.gbr.datasource.notes.entity.TextNoteEntity

@Database(
    entities = [
        TextNoteEntity::class,
        TagEntity::class,
        NoteTagEntity::class,
        ReadingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(NotesTypeConverters::class)
abstract class UserNotesDatabase : RoomDatabase() {
    abstract fun textNoteDao(): TextNoteDao
    abstract fun tagDao(): TagDao
    abstract fun noteTagDao(): NoteTagDao
    abstract fun readingDao(): ReadingDao
}
