package com.javascouts.javascouting2.room;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "matches")
public class Match {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "match_number")
    public int matchNumber;

    @ColumnInfo(name = "blue1")
    public int blue1;

    @ColumnInfo(name = "blue2")
    public int blue2;

    @ColumnInfo(name = "red1")
    public int red1;

    @ColumnInfo(name = "red2")
    public int red2;

}
