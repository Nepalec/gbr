package com.gbr.data.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromNumeric(value: String?): Double? {
        return value?.toDoubleOrNull()
    }

    @TypeConverter
    fun toNumeric(double: Double?): String? {
        return double?.toString()
    }
}
