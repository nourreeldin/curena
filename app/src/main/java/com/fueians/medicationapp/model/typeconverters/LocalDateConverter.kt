package com.fueians.medicationapp.model.typeconverters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }
}
