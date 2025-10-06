package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meanings")
data class Meaning(
    @PrimaryKey
    val _id: Int,
    val txt_id: Double?,
    val name: String?,
    val word: String?,
    val wordt: String?,
    val link: String?,
    val txtname: String?,
    val item_no: Double?
)
