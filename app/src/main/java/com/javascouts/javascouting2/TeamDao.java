package com.javascouts.javascouting2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TeamDao {

    @Query("SELECT * FROM teams")
    List<Team> getAll();

    @Query("SELECT * FROM teams WHERE team_number = :tN")
    Team getTeamByTeamNumber(int tN);

    @Query("SELECT * FROM teams ORDER BY team_number")
    List<Team> getAllSortByTeamNumber();

    @Insert
    void insertAll(Team... teams);

    @Delete
    void delete(Team team);

}
