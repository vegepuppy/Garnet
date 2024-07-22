package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;


public class GarnetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "garnetDatabase.db";// 须加上扩展名
    private static final int DB_VERSION = 1;
    private static final String TABLE_TITLE = "TITLE";
    private static final String TABLE_LINK = "LINK";
    private static final String TABLE_TODO = "TODO";

    // 构造函数
    public GarnetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建两个表
        if (oldVersion < 1) {
            // 信息条目的标题
            db.execSQL("CREATE TABLE TITLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT);");

            // 信息条目的链接
            db.execSQL("CREATE TABLE LINK (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "URI TEXT," +
                    "BELONG INT);");

            // 待办事项的相关信息：名称、日期、是否已经完成（0或1）
            db.execSQL("CREATE TABLE TODO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TASK TEXT," +
                    "DUE TEXT," +
                    "DONE INTEGER);");
            /* 加入示例数据，注意这里不能使用与在MainActivity中一样，通过mDataBaseHelper添加示例
            * 原因是此时的GarnetDatabaseHelper尚未完成初始化（没有Context）
            * 只能通过使用OnUpgrade()方法中传入的db变量进行数据库操作*/
            insertSampleTodo(db,"学习高数", "2024-07-19");
            insertSampleTodo(db,"练习吉他", "2024-07-23");
            insertSampleTodo(db,"训练口语", "2024-09-16");
            insertSampleTodo(db,"英语听说", "2024-09-16");

            insertSampleInfoGroup(db,"高等数学");
            insertSampleInfo(db,"高数链接1", 1);
            insertSampleInfo(db,"高数链接2", 1);
            insertSampleInfo(db,"高数链接3", 1);

            insertSampleInfoGroup(db,"Android开发");
            insertSampleInfo(db,"RecyclerView", 2);
            insertSampleInfo(db,"CheckBox", 2);
            insertSampleInfo(db,"TextView", 2);

            insertSampleInfoGroup(db,"程序设计");
            insertSampleInfo(db,"C语言", 3);
            insertSampleInfo(db,"Python", 3);
            insertSampleInfo(db,"Java", 3);
        }
    }

    public TodoItem insertTodo(TodoItem item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues c = new ContentValues();
            c.put("DUE", item.getDueDate());
            c.put("DONE", item.isDone());
            c.put("TASK", item.getTask());

            // insert()方法返回的就是被插入地方的_id
            long id = db.insert(TABLE_TODO, null, c);
            return new TodoItem(item.getTask(), item.getDueDate(), id, item.isDone());
        }
    }

    private void insertSampleTodo(SQLiteDatabase db,String todoTask, String dueDate) {
       ContentValues c = new ContentValues();
        c.put("TASK", todoTask);
        c.put("DUE",dueDate);
        c.put("DONE",0);
        db.insert(TABLE_TODO,null,c);
    }

    public InfoItem insertInfo(InfoItem item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) { // 使用这样的try-with-resources可以自动调用close()方法，不用自己手动db.close()
            ContentValues contentValues = new ContentValues();
            contentValues.put("URI", item.getUri());
            contentValues.put("BELONG", item.getBelong());
            long id = db.insert(TABLE_LINK, null, contentValues);
            return new InfoItem(item.getUri(), item.getBelong(), id);
        }
    }

    private void insertSampleInfo(SQLiteDatabase db, String uri, long belong){
        ContentValues contentValues = new ContentValues();
        contentValues.put("URI",uri);
        contentValues.put("BELONG",belong);
        db.insert(TABLE_LINK,null,contentValues);
    }

    public InfoGroup insertInfoGroup(InfoGroup infoGroup) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_TITLE, infoGroup.getName());
            long id = db.insert(TABLE_TITLE, null, contentValues);
            return new InfoGroup(infoGroup.getName(), id);
        }
    }

    private void insertSampleInfoGroup(SQLiteDatabase db, String infoGroupName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", infoGroupName);
        db.insert(TABLE_TITLE,null,contentValues);
    }

    public List<TodoItem> loadTodo() {
        List<TodoItem> ret = new ArrayList<>();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
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
    }

    public List<InfoItem> loadInfo(long infoGroupId) {
        List<InfoItem> ret = new ArrayList<>();
        String infoGroupIdString = String.valueOf(infoGroupId);
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query("LINK",
                    new String[]{"_id", "URI", "BELONG"},
                    "BELONG = ?", new String[]{infoGroupIdString}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    //符合BELONG的话，存进来
                    String titleFound = cursor.getString(1); // TODO: 2024-07-19 这里是Info的内容，真的叫Title吗
                    long idFound = cursor.getLong(0);

                    ret.add(new InfoItem(titleFound, infoGroupId, idFound));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return ret;
        }
    }

    public List<InfoGroup> loadInfoGroup() {
        List<InfoGroup> ret = new ArrayList<>();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query(TABLE_TITLE,
                    new String[]{"_id", "NAME"},
                    null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    final int idIdx = 0;
                    final int titleIdx = 1;

                    long idFound = cursor.getLong(idIdx);
                    String titleFound = cursor.getString(titleIdx);

                    InfoGroup infoGroup = new InfoGroup(titleFound, idFound);

                    ret.add(infoGroup);
                } while ((cursor.moveToNext()));
            }
            cursor.close();
            return ret;
        }
    }

    public void deleteInfoGroup(InfoGroup infoGroup){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            String idString = String.valueOf(infoGroup.getId());
            db.delete(TABLE_TITLE,"_id=?",new String[]{idString});
        }
    }

    public void deleteInfo(InfoItem item){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            String idString = String.valueOf(item.getId());
            db.delete("LINK","_id=?",new String[]{idString});
        }
    }

    public void updateTodoStatus(TodoItem ti, CheckBox cb) {
        try (SQLiteDatabase db = this.getWritableDatabase()){
            long id = ti.getId();
            String newIsCheckedString = cb.isChecked() ? "1" : "0";
            String idString = String.valueOf(id);
            ContentValues contentValues = new ContentValues();
            contentValues.put("DONE", newIsCheckedString);
            db.update(TABLE_TODO, contentValues, "_id = ?", new String[]{idString});
        }
    }

    public InfoGroup updateInfoGroup(InfoGroup group, String newTitle){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            String idString = String.valueOf(group.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", newTitle);
            db.update(TABLE_TITLE, contentValues, "_id = ?", new String[]{idString});
            return new InfoGroup(newTitle, group.getId());
        }
        }
}
