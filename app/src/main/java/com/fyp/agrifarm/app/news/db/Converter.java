package com.fyp.agrifarm.app.news.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;


public class Converter {


    @TypeConverter
    public static ArrayList<String> fromStringToArray(String value) {
        return new ArrayList<>(Arrays.asList(value.split("#")));
    }

    @TypeConverter
    public static String arrayToString(ArrayList<String> arrayList) {
        StringBuilder builder = new StringBuilder();
        for (String item : arrayList) {

            builder.append(item);
            builder.append("#");
        }
        builder.deleteCharAt(builder.lastIndexOf("#"));
        return builder.toString();
    }

}
