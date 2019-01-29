package com.javascouts.javascouting2.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Team.class, Match.class}, version = 3, exportSchema = false)
public abstract class TeamDatabase extends RoomDatabase {
    private static TeamDatabase INSTANCE;

    public abstract UserDao TeamDao();

    public static TeamDatabase getTeamDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), TeamDatabase.class, "team-database")
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {

        INSTANCE = null;

    }

    public UserDao getTeamDao() {

        return TeamDao();

    }

}
