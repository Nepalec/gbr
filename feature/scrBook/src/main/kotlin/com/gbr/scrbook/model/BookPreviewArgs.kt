package com.gbr.scrbook.model

import com.gbr.model.gitabase.GitabaseID
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Navigation arguments for BookPreview screen
 */
@Serializable
data class BookPreviewArgs(
    @Contextual val gitabaseId: GitabaseID,
    val bookId: Int
)
