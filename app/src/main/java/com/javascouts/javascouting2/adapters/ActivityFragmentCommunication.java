package com.javascouts.javascouting2.adapters;

import com.javascouts.javascouting2.room.UserDao;
import com.javascouts.javascouting2.room.TeamDatabase;

public interface ActivityFragmentCommunication {

    TeamDatabase getDb();
    void setDb(TeamDatabase db);
    UserDao getDao();
    void setDao(UserDao dao);

    String getCurrent();

    void setCurrent(String current);

}
