package com.gbr.model.notes

import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID

data class TextNote(
    val id: Int,
    val gb: GitabaseID,
    val book: BookPreview?,
    val bookId: Int? = null,
    val bookCode: String? = null, // if sqlite value was MYOWN, then NotePlace is NOT_A_TEXT - general note
    val chapter: Int? = null,
    val textNo: String,
    val textId: String,
    val type: NoteType,
    val place: NotePlace,

    val selectedText: String? = null,
    val selectedTextStart: Int? = null,
    val selectedTextEnd: Int? = null,
    val selectedTextScrollPos: Int? = null,

    val userComment: String? = null,
    val userSubject: String? = null,

    val dateCreated: Int? = null,
    val dateModified: Int? = null,
) {
    val isHighlighted: Boolean
        get(): Boolean {
            return selectedText != null &&
                place != NotePlace.ALL && type!=NoteType.BOOKMARK
        }
}

enum class NotePlace(val value: Int) {
    ALL(0), //fav or note/question to entire text
    SHLOKA(1), //text note specific to text title
    PURPORT(2), //text note specific to purport of the text
}

enum class NoteType(val value: Int) {
    FAV(1), // texts with star
    QUESTION(2),//question to fragment or to entire text
    NOTE(3),//comment to selected fragment or to entire text
    HIGHLIGHT(4), //highlighted fragment without any added text from user
    MY_QUEST(5), //question not related to any text
    MY_NOTE(6),//note not related to any text
    BOOKMARK(7) //this will come from bms table as all bookmarks will be merged into notes
}
