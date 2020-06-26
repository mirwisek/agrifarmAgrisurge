package com.fyp.agrifarm.app.youtube.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.api.client.util.DateTime
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Entity(tableName = ExtendedVideo.TABLE_NAME)
data class ExtendedVideo (
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: String,

        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "thumbnail")
        val thumbnail: String,

        @ColumnInfo(name = "channelTitle")
        val channelTitle: String,

        @ColumnInfo(name = "duration")
        var duration: String,

        @ColumnInfo(name = "publishedDate")
        val publishedDate: DateTime,

        @ColumnInfo(name = "likes")
        val likesCount: Long,

        @ColumnInfo(name = "dislikes")
        val dislikesCount: Long,

        @ColumnInfo(name = "commentsCount")
        val commentsCount: Long,

        @ColumnInfo(name = "views")
        val viewsCount: Long
) {

    companion object {
        const val TABLE_NAME: String = "videosTable"
    }

    fun reformatDuration() {
        if (duration.isEmpty()) return
        val pattern = Pattern.compile("\\d+")
        val m = pattern.matcher(duration)
        val sb = StringBuilder()
        while (m.find()) {
            sb.append(m.group())
            sb.append(":")
        }
        duration = sb.toString().substring(0, sb.length - 1)
    }

    fun getFormattedPublishDate(): String {

        val published = Date(publishedDate.value)
        return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(published)
    }
}