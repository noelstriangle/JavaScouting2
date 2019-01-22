package com.javascouts.javascouting2.adapters;

import com.javascouts.javascouting2.room.TeamDao;
import com.javascouts.javascouting2.room.TeamDatabase;

public interface ActivityFragmentCommunication {

    TeamDatabase getDb();
    void setDb(TeamDatabase db);
    TeamDao getDao();
    void setDao(TeamDao dao);

    String getCurrent();

    void setCurrent(String current);

}
