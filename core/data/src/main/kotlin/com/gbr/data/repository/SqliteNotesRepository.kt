package com.gbr.data.repository

import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote

interface SqliteNotesRepository {
    fun getNotes(): List<TextNote>
    fun getReadings(): List<Reading>
    fun getTags(): List<Tag>
    fun getNoteTags(): List<NoteTag>
    
    fun setNotes(notes: List<TextNote>)
    fun setReadings(readings: List<Reading>)
    fun setTags(tags: List<Tag>)
    fun setNoteTags(noteTags: List<NoteTag>)
    
    fun clearAll()
}

