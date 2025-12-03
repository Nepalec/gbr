package com.gbr.datastore.model

import kotlinx.serialization.Serializable

/**
 * Data model for cached Gitabase data in DataStore.
 * This is the datastore-specific representation of a Gitabase.
 */
@Serializable
data class CachedGitabase(
    val gbname: String,
    val gbalias: String,
    val gblang: String,
    val downloadURL: String,
    val lastModified: String
)