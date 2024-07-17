package com.example.garnet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseOperator {
    private DataBaseOperator() {}

    //私有构造函数避免外部创建实例
    private static DataBaseOperator instance = null; // 单例
    private static SQLiteDatabase db = null; // 数据库

    // 在MainActivity中调用这方法进行初始化，利用SQLiteOpenHelper构造单例
    public static void init(Context context) {
        if (instance == null) {
            instance = new DataBaseOperator();
        }

        GarnetDatabaseHelper dbHelper = new GarnetDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // 获取单例的方法
    public static DataBaseOperator getInstance() {
        return instance;
    }

    public void addTodo(TodoItem ti) {
        // TODO: 2024-07-17 重构这
    }

    public void setTodoStatus(TodoItem ti) {
        long id = ti.getId();
        String newIsCheckedString = ti.getCheckBox().isChecked() ? "1" : "0";
        String idString = String.valueOf(id);
        db.execSQL("UPDATE TODO SET DONE = ? WHERE _id = ?",
                new String[]{newIsCheckedString, idString});
    }

    public void closeDataBase(){
        db.close();
    }
}

