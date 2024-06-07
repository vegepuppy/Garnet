package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
        if(oldVersion < 1){
            // 信息条目的标题
            db.execSQL("CREATE TABLE TITLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT);");

            // 信息条目的链接
            db.execSQL("CREATE TABLE LINK (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "URI TEXT," +
                    "BELONG TEXT);");

            // 待办事项的相关信息：名称、日期、是否已经完成（0或1）
            db.execSQL("CREATE TABLE TODO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TASK TEXT," +
                    "DUE TEXT," +
                    "DONE INTEGER);" );

            // 添加示例内容

            // 示例链接标题和链接内容
            for (int i = 0; i < 5; i ++){
                insertTitle(db,"SAMPLE TITLE #"+i);
            }

            for (int i = 0; i < 3; i ++){
                //注意这里是3 < 5所以可以直接传i进去
                insertLink(db,"SAMPLE LINK #"+i,"SAMPLE TITLE #"+i);
            }

            // 示例待办事项
            for (int i = 0; i < 3; i++){
                insertTodo(db,"SAMPLE TODO #"+i,"2024-05-0"+(i+2));
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

    private static void insertTodo(SQLiteDatabase db, String todoTask, String dueDate){
        ContentValues c = new ContentValues();
        c.put("TASK", todoTask);
        c.put("DUE",dueDate);
        c.put("DONE",0);
        db.insert("TODO",null,c);
    }
}
