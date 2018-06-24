package com.codelab.roomdb.data.database.helpers;

import android.arch.persistence.room.Room;
import android.content.Context;

public class AppDb {

    private static DaoHelper daoHelper;

    public static DaoHelper getAppDb(Context context) {
        if(daoHelper == null) {
            daoHelper = Room.databaseBuilder(context, DaoHelper.class, DaoHelper.DATABASE_NAME).build();
        }
        return daoHelper;
    }
}
