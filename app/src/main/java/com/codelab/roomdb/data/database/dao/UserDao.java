package com.codelab.roomdb.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.codelab.roomdb.data.database.entity.UserDbModel;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM UserDbModel ORDER BY id DESC")
    List<UserDbModel> getAll();

    @Query("SELECT * FROM UserDbModel WHERE name LIKE :name ORDER BY id DESC")
    List<UserDbModel> findByName(String name);

    @Insert
    void insertAll(List<UserDbModel> products);

    @Update
    void update(UserDbModel product);

    @Delete
    void delete(UserDbModel product);

    @Query("DELETE FROM UserDbModel")
    void clearAll();
}
