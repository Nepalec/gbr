package com.gbr.model.book

import android.health.connect.datatypes.units.Volume

/**
 * Domain model representing a Book from a gitabase.
 * This is the clean domain model separate from database entities.
 */
data class BookPreview(
    val id: Int,
    val sort: Int,//book sort if not multi-volume or volume (a.k.a. song) sort
    val title: String, //book title if not multi-volume or volume (a.k.a. song) title
    val author: String,
    val description: String?,
    val type: String,
    val level: Int,
    val structure: BookStructure,
    val colorBack: String?,
    val colorFore: String?,

    //for multi-volume book (group of volumes)
    val volumeBookTitle: String?, //title for group of volumes
    val volumeAbbrev:String?, //abbreviation for group of volumes
    val volumeBookSort: Int?,   //sort for group of volumes
    val volumeBookId: Int?,     //id for group of volumes

    val volumeNumber: Int? //this book volume number in a group of volumes
)
