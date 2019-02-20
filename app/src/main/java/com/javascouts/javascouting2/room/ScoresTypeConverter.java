package com.javascouts.javascouting2.room;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoresTypeConverter {

    @TypeConverter
    public static List<Integer> toList(String value) {

        Integer[] temp = new Integer[6];

        if (value.length() > 0) {
            String[] ts = value.split("-");
            temp[0] = Integer.valueOf(ts[0]);
            temp[1] = Integer.valueOf(ts[1]);
            temp[2] = Integer.valueOf(ts[2]);
            temp[3] = Integer.valueOf(ts[3]);
            temp[4] = Integer.valueOf(ts[4]);
            temp[5] = Integer.valueOf(ts[5]);
        }

        return new ArrayList<>(Arrays.asList(temp));

    }

    @TypeConverter
    public static String toString(List<Integer> value) {

        String result = "";

        for (Integer i : value) {

            result = result.concat(i.toString() + "-");

        }

        if (result.length() != 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;

    }


}
