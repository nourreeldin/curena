package com.fueians.medicationapp.model.typeconverters

import androidx.room.TypeConverter

/**

Type converters for List<String> used in DrugInfoEntity*,
Converts lists to a single string and back.,
Stored format: comma-separated values*/
class StringListConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(separator = "") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split("").map { it.trim() }.filter { it.isNotEmpty() }
    }
}