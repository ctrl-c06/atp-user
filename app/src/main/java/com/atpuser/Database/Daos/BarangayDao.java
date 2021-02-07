package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.atpuser.Database.Models.Barangay;

@Dao
public interface BarangayDao {
    @Insert
    void create(Barangay barangay);

    @Query("SELECT UPPER(name) from barangays WHERE city_code = :municipal_code")
    String[] getByMunicipal(String municipal_code);

    @Query("SELECT name from barangays")
    String[] get();
}
