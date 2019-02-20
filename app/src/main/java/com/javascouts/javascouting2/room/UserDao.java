package com.javascouts.javascouting2.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM teams")
    List<Team> getAllTeams();

    @Query("SELECT * FROM matches")
    List<Match> getAllMatches();

    @Query("SELECT * FROM teams WHERE team_number = :tN")
    Team getTeamByTeamNumber(int tN);

    @Query("SELECT * FROM teams WHERE id = :id")
    Team getTeamById(int id);

    @Query("SELECT * FROM teams ORDER BY team_number")
    List<Team> getAllSortByTeamNumber();

    @Query("SELECT * FROM matches WHERE match_number = :mN")
    Match getMatchByMatchNumber(int mN);

    @Query("SELECT * FROM matches WHERE id = :id")
    Match getMatchById(int id);

    @Query("SELECT * FROM matches ORDER BY match_number")
    List<Match> getAllMatchesSortByMatchNumber();

    @Insert
    void insertTeams(Team... teams);

    @Insert
    void insertMatches(Match... matches);

    @Update
    void updateMatch(Match match);

    @Update
    void updateTeams(Team... teams);

    @Delete
    void deleteTeam(Team team);

    @Delete
    void deleteMatch(Match match);

}
