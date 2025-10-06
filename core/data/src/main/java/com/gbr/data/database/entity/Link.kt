package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class Link(
    @PrimaryKey
    val _id: Int,
    val link: String?,
    val word: String?,
    val text: String?,
    val num_items: Double?
)
