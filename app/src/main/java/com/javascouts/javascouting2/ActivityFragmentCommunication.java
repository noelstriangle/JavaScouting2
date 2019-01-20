package com.javascouts.javascouting2;

public interface ActivityFragmentCommunication {

    TeamDatabase getDb();
    void setDb(TeamDatabase db);
    TeamDao getDao();
    void setDao(TeamDao dao);

    String getCurrent();

    void setCurrent(String current);

}
