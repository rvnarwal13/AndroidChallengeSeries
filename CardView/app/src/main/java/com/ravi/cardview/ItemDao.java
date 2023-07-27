package com.ravi.cardview;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insertItem(DataModel dataModel);

    @Query("SELECT * from items")
    List<DataModel> getAllItems();

    @Query("SELECT * from items ORDER BY id DESC")
    List<DataModel> getAllItemsInReverseOrder();

    @Delete
    void deleteItem(DataModel item);

    @Query("DELETE FROM items")
    void deleteAllItems();
}

