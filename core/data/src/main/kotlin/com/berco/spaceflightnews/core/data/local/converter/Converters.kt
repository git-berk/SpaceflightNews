package com.berco.spaceflightnews.core.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {

    // Stored as a JSON array rather than a delimited string: author names contain
    // commas ("Alejandro Alcantarilla Romera and Chris Bergin"), so no single
    // separator character is safe.
    @TypeConverter
    fun fromAuthors(authors: List<String>): String = Json.encodeToString(authors)

    @TypeConverter
    fun toAuthors(value: String): List<String> =
        if (value.isBlank()) emptyList() else Json.decodeFromString(value)
}
