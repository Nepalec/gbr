package com.gbr.datasource.notes.mapper

import com.gbr.datasource.notes.converters.NotesTypeConverters
import com.gbr.datasource.notes.entity.NoteTagEntity
import com.gbr.datasource.notes.entity.ReadingEntity
import com.gbr.datasource.notes.entity.TagEntity
import com.gbr.datasource.notes.entity.TextNoteEntity
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.NoteType
import com.gbr.model.notes.NotePlace

/**
 * Extension functions to convert Room entities to domain models
 */
fun TextNoteEntity.toDomain(): TextNote {
    return TextNote(
        id = id,
        gb = NotesTypeConverters.toGitabaseID(gb),
        book = null, // BookPreview is not stored in entity, reconstruct if needed
        bookId = bookId,
        bookCode = bookCode,
        chapter = chapter,
        textNo = textNo,
        textId = textId,
        type = NotesTypeConverters.toNoteType(type),
        place = NotesTypeConverters.toNotePlace(place),
        selectedText = selectedText,
        selectedTextStart = selectedTextStart,
        selectedTextEnd = selectedTextEnd,
        selectedTextScrollPos = selectedTextScrollPos,
        userComment = userComment,
        userSubject = userSubject,
        dateCreated = dateCreated,
        dateModified = dateModified
    )
}

/**
 * Extension function to convert domain model to Room entity
 */
fun TextNote.toEntity(): TextNoteEntity {
    return TextNoteEntity(
        id = id,
        gb = NotesTypeConverters.fromGitabaseID(gb),
        bookId = bookId,
        bookCode = bookCode,
        chapter = chapter,
        textNo = textNo,
        textId = textId,
        type = NotesTypeConverters.fromNoteType(type),
        place = NotesTypeConverters.fromNotePlace(place),
        selectedText = selectedText,
        selectedTextStart = selectedTextStart,
        selectedTextEnd = selectedTextEnd,
        selectedTextScrollPos = selectedTextScrollPos,
        userComment = userComment,
        userSubject = userSubject,
        dateCreated = dateCreated,
        dateModified = dateModified
    )
}

fun TagEntity.toDomain(): Tag {
    return Tag(
        id = id,
        name = name,
        categoryId = categoryId
    )
}

fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = id,
        name = name,
        categoryId = categoryId
    )
}

fun NoteTagEntity.toDomain(): NoteTag {
    return NoteTag(
        id = id,
        noteId = noteId,
        tagId = tagId
    )
}

fun NoteTag.toEntity(): NoteTagEntity {
    return NoteTagEntity(
        id = id,
        noteId = noteId,
        tagId = tagId
    )
}

fun ReadingEntity.toDomain(): Reading {
    return Reading(
        gb = NotesTypeConverters.toGitabaseID(gb),
        book_id = book_id,
        volumeNo = volumeNo,
        chapterNo = chapterNo,
        textNo = textNo,
        levels = levels,
        textId = textId,
        author = author,
        title = title,
        subtitle = subtitle,
        textCode = textCode,
        scroll = scroll,
        progress = progress,
        created = created,
        modified = modified,
        scratch = scratch,
        readingTime = readingTime
    )
}

fun Reading.toEntity(): ReadingEntity {
    return ReadingEntity(
        id = 0, // Auto-generated
        gb = NotesTypeConverters.fromGitabaseID(gb),
        book_id = book_id,
        volumeNo = volumeNo,
        chapterNo = chapterNo,
        textNo = textNo,
        levels = levels,
        textId = textId,
        author = author,
        title = title,
        subtitle = subtitle,
        textCode = textCode,
        scroll = scroll,
        progress = progress,
        created = created,
        modified = modified,
        scratch = scratch,
        readingTime = readingTime
    )
}
