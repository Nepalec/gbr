package com.gbr.data.repository

import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqliteNotesRepositoryImpl @Inject constructor() : SqliteNotesRepository {
    
    private val _notes = MutableStateFlow<List<TextNote>>(emptyList())
    private val _readings = MutableStateFlow<List<Reading>>(emptyList())
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    private val _noteTags = MutableStateFlow<List<NoteTag>>(emptyList())
    
    override fun getNotes(): List<TextNote> = _notes.value
    
    override fun getReadings(): List<Reading> = _readings.value
    
    override fun getTags(): List<Tag> = _tags.value
    
    override fun getNoteTags(): List<NoteTag> = _noteTags.value
    
    override fun setNotes(notes: List<TextNote>) {
        _notes.value = notes
    }
    
    override fun setReadings(readings: List<Reading>) {
        _readings.value = readings
    }
    
    override fun setTags(tags: List<Tag>) {
        _tags.value = tags
    }
    
    override fun setNoteTags(noteTags: List<NoteTag>) {
        _noteTags.value = noteTags
    }
    
    override fun clearAll() {
        _notes.value = emptyList()
        _readings.value = emptyList()
        _tags.value = emptyList()
        _noteTags.value = emptyList()
    }
}

