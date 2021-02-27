package com.atpuser.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.atpuser.Database.Models.User;

import java.util.List;


@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long create(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE id =:id")
    User find(int id);

    @Query("SELECT * FROM users WHERE id =:id")
    User find(long id);


    @Query("SELECT * FROM users WHERE phone_number =:phone_number")
    User findByPhone(String phone_number);

    @Query("SELECT count(*) FROM users WHERE firstname=:firstname AND middlename=:middlename AND lastname=:lastname AND date_of_birth=:birthdate")
    int isUserAlreadyExists(String firstname, String middlename, String lastname, String birthdate);
}
