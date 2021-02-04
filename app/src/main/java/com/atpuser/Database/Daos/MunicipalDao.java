package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.atpuser.Database.Models.Municipal;

import java.util.List;

@Dao
public interface MunicipalDao {
    @Insert
    void create(Municipal municipal);

    @Query("SELECT name FROM municipals")
    List<String> all();

    @Query("SELECT * FROM municipals")
    List<Municipal> get();

    @Query("SELECT name FROM municipals WHERE province_id = :province_id")
    List<String> findByProvince(int province_id);

    @Query("SELECT id FROM municipals WHERE name = :name")
    int getIdByName(String name);
}
