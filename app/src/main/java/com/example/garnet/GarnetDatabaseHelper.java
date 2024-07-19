package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GarnetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "garnetDatabase.db";// 须加上扩展名
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
            // 加入示例数据
            // TODO: 2024-07-19 我本来想在DataBaseAction那个类里面写一个方法用于添加示例数据的，但是无法调用，或者只能在init()里面调用，每次进入都会反复加入示例
            insertTodo(db, "学习高数", "2024-7-19");
            insertTodo(db, "练习吉他", "2024-7-23");
            insertTodo(db, "训练口语", "2024-9-16");
            insertTodo(db, "英语听说", "2024-9-16");

            insertTitle(db,"高等数学");
            insertLink(db,"高数链接1","高等数学");
            insertLink(db,"高数链接2","高等数学");
            insertLink(db,"高数链接3","高等数学");

            insertTitle(db,"Android开发");
            insertLink(db,"RecyclerView","Android开发");
            insertLink(db,"CheckBox","Android开发");
            insertLink(db,"TextView","Android开发");

            insertTitle(db,"程序设计");
            insertLink(db,"C语言","程序设计");
            insertLink(db,"Python","程序设计");
            insertLink(db,"Java","程序设计");
            }
        }
    // 上面的是书上的代码修改而成

    // 下面这三个private static Void 只用于初始化，有问题
    // TODO: 2024-07-17 重构
    private void insertTitle(SQLiteDatabase db, String title){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", title);
        db.insert("TITLE",null,contentValues);
    }

    private void insertLink(SQLiteDatabase db, String uri, String belong){
        ContentValues contentValues = new ContentValues();
        contentValues.put("URI",uri);
        contentValues.put("BELONG",belong);
        db.insert("LINK",null,contentValues);
    }

    private void insertTodo(SQLiteDatabase db, String todoTask, String dueDate){
        ContentValues c = new ContentValues();
        c.put("TASK", todoTask);
        c.put("DUE",dueDate);
        c.put("DONE",0);
        db.insert("TODO",null,c);
    }

}
