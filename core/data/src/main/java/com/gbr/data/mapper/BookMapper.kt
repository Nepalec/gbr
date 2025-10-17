package com.gbr.data.mapper

import com.gbr.data.database.entity.Book as BookEntity
import com.gbr.model.book.Book as BookDomain

/**
 * Mapper functions to convert between database entities and domain models.
 */

/**
 * Converts a database Book entity to a domain Book model.
 * 
 * @param entity The database entity
 * @return The domain model
 */
fun BookEntity.toDomainModel(): BookDomain {
    return BookDomain(
        id = _id ?: 0,
        sort = sort,
        author = author,
        title = title,
        desc = desc,
        type = type,
        levels = levels,
        hasSanskrit = hasSanskrit ?: 0,
        hasPurport = hasPurport ?: 1,
        hasColorStructure = hasColorStructure,
        isSongBook = isSongBook ?: 0,
        textSize = text_size,
        purportSize = purport_size,
        textBeginRaw = text_begin_raw ?: 0,
        textEndRaw = text_end_raw ?: 0,
        webAbbrev = web_abbrev,
        compareCode = compare_code,
        issue = issue,
        isSimple = isSimple ?: 0
    )
}

/**
 * Converts a list of database Book entities to a list of domain Book models.
 * 
 * @param entities The list of database entities
 * @return The list of domain models
 */
fun List<BookEntity>.toDomainModels(): List<BookDomain> {
    return map { it.toDomainModel() }
}
