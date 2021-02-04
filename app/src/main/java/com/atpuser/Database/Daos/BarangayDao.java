package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.atpuser.Database.Models.Barangay;

@Dao
public interface BarangayDao {
    @Insert
    void create(Barangay barangay);

    @Query("SELECT name from barangays WHERE municipal_id = :municipal_id")
    String[] getByMunicipal(int municipal_id);

    @Query("SELECT name from barangays")
    String[] get();
}
