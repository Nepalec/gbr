package com.gbr.network

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.notes.NotePlace
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.NoteType
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesBackupImportDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : INotesBackupImportDataSource {

    companion object {
        private const val TAG = "NotesBackupImportDataSource"
    }

    override suspend fun importFromDatabaseFile(fileUri: String): NotesBackupImportResult =
        withContext(Dispatchers.IO) {
            try {
                val filePath = getFilePathFromUri(fileUri) ?: run {
                    return@withContext NotesBackupImportResult(
                        notes = emptyList(),
                        readings = emptyList(),
                        tags = emptyList(),
                        noteTags = emptyList(),
                        success = false,
                        error = "Failed to resolve file path from URI: $fileUri"
                    )
                }

                val database = SQLiteDatabase.openDatabase(
                    filePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )

                try {
                    val notes = readNotes(database)
                    val readings = readReadings(database)
                    val tags = readTags(database)
                    val noteTags = readNoteTags(database)

                    NotesBackupImportResult(
                        notes = notes,
                        readings = readings,
                        tags = tags,
                        noteTags = noteTags,
                        success = true
                    )
                } finally {
                    database.close()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error importing database", e)
                NotesBackupImportResult(
                    notes = emptyList(),
                    readings = emptyList(),
                    tags = emptyList(),
                    noteTags = emptyList(),
                    success = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }

    private fun getFilePathFromUri(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                uri.path
            } else {
                // For content:// URIs, copy to temp file
                val originalFileName = getOriginalFileName(uri)
                val tempFile = if (originalFileName != null) {
                    File(context.cacheDir, originalFileName)
                } else {
                    File.createTempFile("notes_backup_", ".db", context.cacheDir)
                }

                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile.absolutePath
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file path from URI", e)
            null
        }
    }

    private fun getOriginalFileName(uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        it.getString(nameIndex)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun readNotes(database: SQLiteDatabase): List<TextNote> {
        val notes = mutableListOf<TextNote>()
        val cursor = database.rawQuery("SELECT * FROM notes", null)

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val note = mapCursorToTextNote(it)
                    if (note != null) {
                        notes.add(note)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error mapping note row", e)
                }
            }
        }

        return notes
    }

    private fun mapCursorToTextNote(cursor: Cursor): TextNote? {
        return try {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val bookID = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"))
            val bookCode = cursor.getString(cursor.getColumnIndexOrThrow("book_code"))
            val lang = cursor.getString(cursor.getColumnIndexOrThrow("lang"))
            val dbtype = cursor.getString(cursor.getColumnIndexOrThrow("dbtype"))
            val chNo = cursor.getString(cursor.getColumnIndexOrThrow("ch_no"))
            val txtNo = cursor.getString(cursor.getColumnIndexOrThrow("txt_no"))
            val txtRowId = cursor.getLong(cursor.getColumnIndexOrThrow("txt_row_id"))
            val type = cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            val place = cursor.getInt(cursor.getColumnIndexOrThrow("place"))
            val scrollpos = cursor.getInt(cursor.getColumnIndexOrThrow("scrollpos"))
            val startpos = cursor.getInt(cursor.getColumnIndexOrThrow("startpos"))
            val endpos = cursor.getInt(cursor.getColumnIndexOrThrow("endpos"))
            val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
            val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))
            val subj = cursor.getString(cursor.getColumnIndexOrThrow("subj"))
            val createddate = cursor.getLong(cursor.getColumnIndexOrThrow("createddate"))
            val modifieddate = cursor.getLong(cursor.getColumnIndexOrThrow("modifieddate"))
            val txtId = cursor.getString(cursor.getColumnIndexOrThrow("txt_id"))

            val gitabaseId = GitabaseID(
                type = GitabaseType(dbtype),
                lang = GitabaseLang(lang)
            )

            // Map note type and place
            val noteType = NoteType.values().find { it.value == type } ?: NoteType.NOTE
            val notePlace = NotePlace.values().find { it.value == place } ?: NotePlace.ALL

            // Determine selectedText vs userComment
            val selectedText = if (place != NotePlace.ALL.value && note.isNotEmpty()) note else null

            TextNote(
                id = id,
                gb = gitabaseId,
                book = null, //we cant get BookPreview now, cause gitabase may be not installed
                bookId = bookID,
                bookCode = bookCode,
                chapter = chNo.toIntOrNull(),
                textNo = txtNo,
                textId = txtId.ifEmpty { txtRowId.toString() },
                type = noteType,
                place = notePlace,
                selectedText = selectedText,
                selectedTextStart = if (startpos >= 0) startpos else null,
                selectedTextEnd = if (endpos >= 0) endpos else null,
                selectedTextScrollPos = if (scrollpos > 0) scrollpos else null,
                userComment = comment.ifEmpty { null },
                userSubject = subj.ifEmpty { null },
                dateCreated = createddate.toInt(),
                dateModified = modifieddate.toInt()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping cursor to TextNote", e)
            null
        }
    }

    private fun readReadings(database: SQLiteDatabase): List<Reading> {
        val readings = mutableListOf<Reading>()
        val cursor = database.rawQuery("SELECT * FROM reading", null)

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val reading = mapCursorToReading(it)
                    if (reading != null) {
                        readings.add(reading)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error mapping reading row", e)
                }
            }
        }

        return readings
    }

    private fun mapCursorToReading(cursor: Cursor): Reading? {
        return try {
            val rowId = cursor.getString(cursor.getColumnIndexOrThrow("row_id"))
            val parts = rowId.split("|")
            if (parts.size != 4) throw Exception("Invalid row_id format: $rowId")

            Reading(
                gb = GitabaseID(
                    type = GitabaseType(parts[0]),
                    lang = GitabaseLang(parts[1])
                ),
                book_id = parts[2].toInt(),
                volumeNo =parts[3].toInt(),
                chapterNo = cursor.getIntOrNull(cursor.getColumnIndexOrThrow("chapter_no")),
                textNo = cursor.getString(cursor.getColumnIndexOrThrow("text_no")),
                levels = cursor.getInt(cursor.getColumnIndexOrThrow("levels")),
                textId = cursor.getString(cursor.getColumnIndexOrThrow("text_id")),
                author = cursor.getString(cursor.getColumnIndexOrThrow("author")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                subtitle = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("subtitle")),
                textCode = cursor.getString(cursor.getColumnIndexOrThrow("text_code")),
                scroll = cursor.getInt(cursor.getColumnIndexOrThrow("scroll")),
                progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress")),
                created = cursor.getLong(cursor.getColumnIndexOrThrow("created")),
                modified = cursor.getLong(cursor.getColumnIndexOrThrow("modified")),
                scratch = cursor.getInt(cursor.getColumnIndexOrThrow("scratch")),
                readingTime = cursor.getLong(cursor.getColumnIndexOrThrow("reading_time"))
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping cursor to Reading", e)
            null
        }
    }

    private fun readTags(database: SQLiteDatabase): List<Tag> {
        val tags = mutableListOf<Tag>()
        val cursor = database.rawQuery("SELECT * FROM tags", null)

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val tag = mapCursorToTag(it)
                    if (tag != null) {
                        tags.add(tag)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error mapping tag row", e)
                }
            }
        }

        return tags
    }

    private fun mapCursorToTag(cursor: Cursor): Tag? {
        return try {
            Tag(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("cat_id"))
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping cursor to Tag", e)
            null
        }
    }

    private fun readNoteTags(database: SQLiteDatabase): List<NoteTag> {
        val noteTags = mutableListOf<NoteTag>()
        val cursor = database.rawQuery("SELECT * FROM tagging", null)

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val noteTag = mapCursorToNoteTag(it)
                    if (noteTag != null) {
                        noteTags.add(noteTag)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error mapping tagging row", e)
                }
            }
        }

        return noteTags
    }

    private fun mapCursorToNoteTag(cursor: Cursor): NoteTag? {
        return try {
            NoteTag(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                noteId = cursor.getInt(cursor.getColumnIndexOrThrow("note_id")),
                tagId = cursor.getInt(cursor.getColumnIndexOrThrow("tag_id"))
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping cursor to NoteTag", e)
            null
        }
    }

    private fun Cursor.getIntOrNull(columnIndex: Int): Int? {
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}

