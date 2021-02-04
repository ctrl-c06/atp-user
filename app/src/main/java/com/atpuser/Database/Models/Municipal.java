package com.atpuser.Database.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="municipals")
public class Municipal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int province_id;

    public String name;

    public Municipal(int province_id, String name) {
        this.province_id = province_id;
        this.name = name;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
