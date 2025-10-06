package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eind")
data class Eind(
    @PrimaryKey
    val _id: Int,
    val txt_id: Double?,
    val name: String?,
    val word: String?,
    val link: String?,
    val txtname: String?
)
