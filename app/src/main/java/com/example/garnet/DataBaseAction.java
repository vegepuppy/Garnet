package com.example.garnet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseAction {
    private DataBaseAction() {}

    //私有构造函数避免外部创建实例
    private static SQLiteDatabase db = null; // 数据库

    // 在MainActivity中调用这方法进行初始化，利用SQLiteOpenHelper构造单例
    public static void init(Context context) {
        GarnetDatabaseHelper dbHelper = new GarnetDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void insertTodoItem(TodoItem ti) {
        // TODO: 2024-07-17 重构这
    }

    // 设置TodoItem是否完成了，并存储到数据库里
    public static void setTodoStatus(TodoItem ti) {
        long id = ti.getId();
        String newIsCheckedString = ti.getCheckBox().isChecked() ? "1" : "0";
        String idString = String.valueOf(id);
        db.execSQL("UPDATE TODO SET DONE = ? WHERE _id = ?",
                new String[]{newIsCheckedString, idString});
    }

    public static void closeDataBase(){
        db.close();
    }
}

