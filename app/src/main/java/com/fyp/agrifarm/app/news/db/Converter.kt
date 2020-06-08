package com.fyp.agrifarm.app.news.db

import androidx.room.TypeConverter
import java.util.*

object Converter {
    @JvmStatic
    @TypeConverter
    fun fromStringToArray(value: String): List<String> {
        return listOf(*value.split("#@#".toRegex()).toTypedArray())
    }

    @JvmStatic
    @TypeConverter
    fun arrayToString(list: List<String?>): String {
        val builder = StringBuilder()
        for (item in list) {
            builder.append(item)
            builder.append("#@#")
        }
        builder.deleteCharAt(builder.lastIndexOf("#@#"))
        return builder.toString()
    }
}