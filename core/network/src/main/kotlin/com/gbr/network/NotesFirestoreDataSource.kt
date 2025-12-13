package com.gbr.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.parseGitabaseID
import com.gbr.model.notes.NotePlace
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.NoteType
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : INotesCloudDataSource {

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_NOTES = "notes"
        private const val COLLECTION_TAGS = "tags"
        private const val COLLECTION_NOTE_TAGS = "noteTags"
    }

    override suspend fun saveNote(userId: String, note: TextNote): Result<Unit> {
        return try {
            val docRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTES)
                .document(note.id.toString())
            
            docRef.set(note.toFirestoreMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(userId: String, note: TextNote): Result<Unit> {
        return saveNote(userId, note) // Firestore set() with same ID updates the document
    }

    override suspend fun deleteNote(userId: String, noteId: Int): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTES)
                .document(noteId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNotes(userId: String): Flow<List<TextNote>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { doc ->
                        doc.toTextNote()
                    }
                    trySend(notes)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override fun observeNotesByBook(
        userId: String,
        gitabaseId: GitabaseID,
        bookId: Int
    ): Flow<List<TextNote>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTES)
            .whereEqualTo("gb", gitabaseId.key)
            .whereEqualTo("bookId", bookId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { doc ->
                        doc.toTextNote()
                    }
                    trySend(notes)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override fun observeNotesByChapter(
        userId: String,
        gitabaseId: GitabaseID,
        bookId: Int,
        chapter: Int
    ): Flow<List<TextNote>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTES)
            .whereEqualTo("gb", gitabaseId.key)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("chapter", chapter)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { doc ->
                        doc.toTextNote()
                    }
                    trySend(notes)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override fun observeNotesByText(
        userId: String,
        gitabaseId: GitabaseID,
        bookId: Int,
        textNo: String
    ): Flow<List<TextNote>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTES)
            .whereEqualTo("gb", gitabaseId.key)
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("textNo", textNo)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { doc ->
                        doc.toTextNote()
                    }
                    trySend(notes)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun saveTag(userId: String, tag: Tag): Result<Unit> {
        return try {
            val docRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TAGS)
                .document(tag.id.toString())
            
            docRef.set(tag.toFirestoreMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTag(userId: String, tag: Tag): Result<Unit> {
        return saveTag(userId, tag)
    }

    override suspend fun deleteTag(userId: String, tagId: Int): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TAGS)
                .document(tagId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTags(userId: String): Flow<List<Tag>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TAGS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val tags = snapshot.documents.mapNotNull { doc ->
                        doc.toTag()
                    }
                    trySend(tags)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun saveNoteTag(userId: String, noteTag: NoteTag): Result<Unit> {
        return try {
            // Use noteId_tagId as document ID for uniqueness
            val docId = "${noteTag.noteId}_${noteTag.tagId}"
            val docRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTE_TAGS)
                .document(docId)
            
            docRef.set(noteTag.toFirestoreMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNoteTag(userId: String, noteTagId: Int): Result<Unit> {
        return try {
            // Note: We need to find the document by noteTagId
            // For now, we'll query and delete. In production, consider storing noteTagId in document
            val query = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTE_TAGS)
                .whereEqualTo("id", noteTagId)
                .limit(1)
            
            val snapshot = query.get().await()
            if (!snapshot.isEmpty) {
                snapshot.documents.first().reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNoteTags(userId: String): Flow<List<NoteTag>> = callbackFlow {
        val listenerRegistration = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTE_TAGS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val noteTags = snapshot.documents.mapNotNull { doc ->
                        doc.toNoteTag()
                    }
                    trySend(noteTags)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun importNotesFromSqlite(userId: String, notes: List<TextNote>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val notesRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTES)
            
            notes.forEach { note ->
                val docRef = notesRef.document(note.id.toString())
                batch.set(docRef, note.toFirestoreMap())
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importTagsFromSqlite(userId: String, tags: List<Tag>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val tagsRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TAGS)
            
            tags.forEach { tag ->
                val docRef = tagsRef.document(tag.id.toString())
                batch.set(docRef, tag.toFirestoreMap())
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importNoteTagsFromSqlite(userId: String, noteTags: List<NoteTag>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val noteTagsRef = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTE_TAGS)
            
            noteTags.forEach { noteTag ->
                val docId = "${noteTag.noteId}_${noteTag.tagId}"
                val docRef = noteTagsRef.document(docId)
                batch.set(docRef, noteTag.toFirestoreMap())
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Extension functions for mapping

    private fun TextNote.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "gb" to gb.key,
            "bookId" to bookId,
            "bookCode" to bookCode,
            "chapter" to chapter,
            "textNo" to textNo,
            "textId" to textId,
            "type" to type.value,
            "place" to place.value,
            "selectedText" to selectedText,
            "selectedTextStart" to selectedTextStart,
            "selectedTextEnd" to selectedTextEnd,
            "selectedTextScrollPos" to selectedTextScrollPos,
            "userComment" to userComment,
            "userSubject" to userSubject,
            "dateCreated" to dateCreated,
            "dateModified" to dateModified
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toTextNote(): TextNote? {
        return try {
            val gbKey = getString("gb") ?: return null
            val gitabaseId = gbKey.parseGitabaseID() ?: return null
            
            TextNote(
                id = getLong("id")?.toInt() ?: return null,
                gb = gitabaseId,
                book = null, // BookPreview not stored in Firestore
                bookId = getLong("bookId")?.toInt(),
                bookCode = getString("bookCode"),
                chapter = getLong("chapter")?.toInt(),
                textNo = getString("textNo") ?: "",
                textId = getString("textId") ?: "",
                type = NoteType.values().find { it.value == (getLong("type")?.toInt() ?: 0) } ?: NoteType.NOTE,
                place = NotePlace.values().find { it.value == (getLong("place")?.toInt() ?: 0) } ?: NotePlace.ALL,
                selectedText = getString("selectedText"),
                selectedTextStart = getLong("selectedTextStart")?.toInt(),
                selectedTextEnd = getLong("selectedTextEnd")?.toInt(),
                selectedTextScrollPos = getLong("selectedTextScrollPos")?.toInt(),
                userComment = getString("userComment"),
                userSubject = getString("userSubject"),
                dateCreated = getLong("dateCreated")?.toInt(),
                dateModified = getLong("dateModified")?.toInt()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Tag.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "categoryId" to categoryId
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toTag(): Tag? {
        return try {
            Tag(
                id = getLong("id")?.toInt() ?: return null,
                name = getString("name") ?: return null,
                categoryId = getLong("categoryId")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun NoteTag.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "noteId" to noteId,
            "tagId" to tagId
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toNoteTag(): NoteTag? {
        return try {
            NoteTag(
                id = getLong("id")?.toInt() ?: 0,
                noteId = getLong("noteId")?.toInt() ?: return null,
                tagId = getLong("tagId")?.toInt() ?: return null
            )
        } catch (e: Exception) {
            null
        }
    }
}

