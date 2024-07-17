package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

    // 将所有读到的TodoItem读入mainList(instanceof = List<TodoGroup>)
    // 返回所有找到的TodoItem构成的列表
    public static List<TodoItem> loadTodo() {
        List<TodoItem> ret = new ArrayList<>();
        Cursor cursor = db.query("TODO",
                new String[]{"_id", "TASK", "DUE", "DONE"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                final int idIdx = 0;
                final int taskIdx = 1;
                final int dueIdx = 2;
                final int doneIdx = 3;

                String dateFound = cursor.getString(dueIdx);
                String taskFound = cursor.getString(taskIdx);
                boolean doneFound = cursor.getInt(doneIdx) > 0;
                long idFound = cursor.getLong(idIdx);

                TodoItem ti = new TodoItem(taskFound, dateFound, idFound, doneFound);

                ret.add(ti);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return ret;
    }

    public static TodoItem insertTodo(TodoItem ti){
        ContentValues c = new ContentValues();
        c.put("DUE",ti.getDueDate());
        c.put("DONE",ti.isDone());
        c.put("TASK",ti.getTask());

        // insert()方法返回的就是被插入地方的_id
        long id = db.insert("TODO", null, c);

        ti.setId(id);

        return ti;
    }

}

