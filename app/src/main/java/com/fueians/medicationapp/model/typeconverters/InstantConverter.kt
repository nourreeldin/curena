package com.fueians.medicationapp.model.typeconverters

import androidx.room.TypeConverter
import java.time.Instant


class InstantConverter {
    @TypeConverter
    fun fromTimestampToInstant(timestamp: Long?): Instant? =
        // if the timestamp is not null return instant, otherwise return null                                                                           1``````````````
        timestamp?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromInstantToTimestamp(instant: Instant?): Long? =
        instant?.toEpochMilli()
}