package com.javascouts.javascouting2;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "teams")
public class Team {

    @PrimaryKey
    public int id;

    @ColumnInfo(name="team_name")
    public String teamName;

    @ColumnInfo(name="team_number")
    public int teamNumber;

    @ColumnInfo(name="can_land")
    public boolean canLand;

    @ColumnInfo(name="can_sample")
    public boolean canSample;

    @ColumnInfo(name="can_claim")
    public boolean canClaim;

    @ColumnInfo(name="can_park")
    public boolean canPark;

    @ColumnInfo(name="num_depot")
    public int depotMinerals;

    @ColumnInfo(name="num_lander")
    public int landerMinerals;

    @ColumnInfo(name="can_latch")
    public int canLatch;

    @ColumnInfo(name="can_endPark")
    public int canEndPark;

}
