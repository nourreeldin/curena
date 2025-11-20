package com.fueians.medicationapp.model.typeconverters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ListStringConverter {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Define the type to be converted (List<String>)
    private val listStringType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listStringType)

    @TypeConverter
    fun fromListString(list: List<String>?): String? {
        // Converts the List<String> object to a single JSON String
        return list?.let { adapter.toJson(it) }
    }

    @TypeConverter
    fun toListString(json: String?): List<String>? {
        // Converts the JSON String back into a List<String> object
        return json?.let { adapter.fromJson(it) }
    }
}