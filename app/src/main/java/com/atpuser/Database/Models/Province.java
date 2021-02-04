package com.atpuser.Database.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="provinces")
public class Province {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    public Province(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
