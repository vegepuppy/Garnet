package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GarnetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "garnetDatabase";
    private static final int DB_VERSION = 1;

    // 构造函数
    public GarnetDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db,oldVersion,newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        //创建两个表
        if(oldVersion<1){
            db.execSQL("CREATE TABLE TITLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT);");
            db.execSQL("CREATE TABLE LINK (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "URI TEXT," +
                    "BELONG TEXT);");

            // 添加示例内容
            for (int i = 0; i<5; i++){
                insertTitle(db,"SAMPLE TITLE #"+i);
            }
            for (int i = 0; i<3; i++){
                //注意这里是3 < 5所以可以直接传i进去
                insertLink(db,"SAMPLE LINK #"+i,"SAMPLE TITLE #"+i);
            }
        }
    }

    // 上面的是书上的代码修改而成


    private static void insertTitle(SQLiteDatabase db, String title){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", title);
        db.insert("TITLE",null,contentValues);
    }

    private static void insertLink(SQLiteDatabase db, String uri, String belong){
        ContentValues contentValues = new ContentValues();
        contentValues.put("URI",uri);
        contentValues.put("BELONG",belong);
        db.insert("LINK",null,contentValues);
    }

}
