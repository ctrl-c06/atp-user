package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.atpuser.Database.Models.Barangay;
import com.atpuser.Database.Models.Municipal;

import java.util.List;

@Dao
public interface BarangayDao {
    @Insert
    void create(Barangay barangay);

    @Query("SELECT * FROM barangays WHERE city_code = :municipal_code ORDER BY name")
    List<Barangay> getByMunicipal(String municipal_code);

    @Query("SELECT name FROM barangays")
    String[] get();

    @Query("SELECT code FROM barangays WHERE name = :name")
    String getCodeByName(String name);

}
