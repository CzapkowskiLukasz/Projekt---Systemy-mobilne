package com.example.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "UserData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table UserData(Id INT primary key, Name TEXT, Temperature TEXT, Icon TEXT, IsActive INT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP Table if exists UserData");
    }

    public Boolean insertCity(String name, String temperature, String icon, int activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Icon", icon);
        contentValues.put("Name", name);
        contentValues.put("Temperature", temperature);
        contentValues.put("IsActive", activity);
        long result = db.insert("UserData", null, contentValues);
        return result != -1;
    }

    public Boolean manageCityActivity(String name, int activity){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IsActive", activity);
        Cursor cursor = DB.rawQuery("Select * from UserData where Name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.update("UserData", contentValues, "Name=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Cursor getCityByActivity(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UserData WHERE IsActive = 1", null);
        return cursor;
    }

    public Boolean updateCity(String name, String temperature, String icon) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Icon", icon);
        contentValues.put("Temperature", temperature);
        Cursor cursor = DB.rawQuery("Select * from UserData where Name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.update("UserData", contentValues, "Name=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean deleteCity (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UserData WHERE Name = ?", new String[] {name});
        if(cursor.getCount()>0){
            long result = db.delete("UserData", "name=?", new String[] {name});
            if (result == -1){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UserData", null);
        return cursor;
    }
}
