package com.atpuser.Database.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="barangays")
public class Barangay {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int municipal_id;
    public int province_id;

    public String name;



    public Barangay(int municipal_id, String name) {
        this.municipal_id = municipal_id;
        this.name = name;
    }

    public int getMunicipal_id() {
        return municipal_id;
    }

    public void setMunicipal_id(int municipal_id) {
        this.municipal_id = municipal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
