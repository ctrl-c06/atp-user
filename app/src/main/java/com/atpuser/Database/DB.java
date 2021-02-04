package com.atpuser.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.atpuser.Database.Daos.BarangayDao;
import com.atpuser.Database.Daos.MunicipalDao;
import com.atpuser.Database.Daos.ProvinceDao;
import com.atpuser.Database.Daos.UserDao;
import com.atpuser.Database.Models.Barangay;
import com.atpuser.Database.Models.Municipal;
import com.atpuser.Database.Models.Province;
import com.atpuser.Database.Models.User;


@Database(entities = {User.class, Province.class, Municipal.class, Barangay.class},version = 1)
public abstract class DB extends RoomDatabase {

    private static DB appDatabase;
    private Context context;
    public abstract UserDao userDao();
    public abstract ProvinceDao provinceDao();
    public abstract MunicipalDao municipalDao();
    public abstract BarangayDao barangayDao();


    public synchronized  static DB getInstance(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), DB.class, "atp_user")
                    .createFromAsset("atp_user.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    public void destroyInstance() {
        appDatabase = null;
    }
}