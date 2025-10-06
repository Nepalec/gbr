package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lettersbytopic")
data class LetterByTopic(
    @PrimaryKey
    val _id: Int,
    val rec_id: String?,
    val code: String?,
    val song: Double?,
    val chapter: Double?,
    val dear: String?,
    val head: String?,
    val topics: Double?
)
