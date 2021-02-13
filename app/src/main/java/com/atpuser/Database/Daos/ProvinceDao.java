package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.atpuser.Database.Models.Province;

import java.util.List;

@Dao
public interface ProvinceDao {
    @Insert
    void create(Province province);

    @Query("SELECT name FROM provinces ORDER BY name")
    List<String> all();

    @Query("SELECT code FROM provinces WHERE name = :name ")
    String getCodeByName(String name);


}
