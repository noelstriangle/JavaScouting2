package com.javascouts.javascouting2.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Team.class}, version = 3, exportSchema = false)
public abstract class TeamDatabase extends RoomDatabase {
    public abstract TeamDao teamDao();
}
