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

    @Query("SELECT name FROM municipals WHERE province_code = :province_code")
    List<String> findByProvince(String province_code);

    @Query("SELECT code FROM municipals WHERE name = :name")
    String getIdByName(String name);
}
