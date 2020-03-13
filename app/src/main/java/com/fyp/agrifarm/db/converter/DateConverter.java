package com.fyp.agrifarm.db.converter;

import androidx.room.TypeConverter;

import com.google.api.client.util.DateTime;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    // ISO 8601 format
    @TypeConverter
    public static DateTime fromTimestamp(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        if (value == null) return null;
        return new DateTime(value);
//        try {
//            Date date = ISO8601Utils.parse(value, new ParsePosition(0));
//            return new DateTime(value);
//            return new DateTime(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    @TypeConverter
    public static String dateToTimestamp(DateTime date){
        return date.toString();
//        return ISO8601Utils.format(new Date(date.getValue()));
    }
}
