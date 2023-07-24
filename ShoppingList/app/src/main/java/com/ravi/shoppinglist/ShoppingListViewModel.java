package com.ravi.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ShoppingListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<DataItem>> dataListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<DataItem>> checkedDataListLiveData = new MutableLiveData<>();
    private ArrayList<DataItem> dataList = new ArrayList<>();
    private ArrayList<DataItem> checkedDataList = new ArrayList<>();
    private DatabaseHelper database;

    public ShoppingListViewModel() {}

    public ShoppingListViewModel(Context context) {
        database = new DatabaseHelper(context.getApplicationContext());
    }

    public LiveData<ArrayList<DataItem>> getDataListLiveData() {
        return dataListLiveData;
    }

    public LiveData<ArrayList<DataItem>> getCheckedListLiveData() {
        return checkedDataListLiveData;
    }

    public ArrayList<DataItem> getDataList() {
        return dataList;
    }

    public ArrayList<DataItem> getCheckedDataList() {
        return checkedDataList;
    }

    public void insertData(DataItem dataItem) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(database.getColumnData(), dataItem.getData());

        long newRowId = db.insert(database.getTableName(), null, values);
        if (newRowId != -1) {
            DataItem newItem = new DataItem();
            newItem.setSerialNumber((int) newRowId);
            newItem.setData(dataItem.getData());
            newItem.setChecked(dataItem.getChecked());

            dataList.add(newItem);
            dataListLiveData.setValue(dataList);
        }
        db.close();
    }

    public void updateData(DataItem dataItem, boolean isChecked) {
        dataItem.setChecked(isChecked);

        dataList.remove(dataItem.getSerialNumber());
        checkedDataList.add(dataItem);

        checkedDataListLiveData.postValue(checkedDataList);
        dataListLiveData.postValue(dataList);
    }
}