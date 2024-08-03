package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GarnetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "garnetDatabase.db";// 须加上扩展名
    private static final int DB_VERSION = 1;
    private static final String TABLE_TITLE = "TITLE";
    private static final String TABLE_LINK = "LINK";
    private static final String TABLE_TODO = "TODO";
    private static final String TABLE_TODO_LINK = "TODO_LINK";

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
                    "BELONG INTEGER," +
                    "DISPLAY TEXT);");

            // 待办事项的相关信息：名称、日期、是否已经完成（0或1）
            db.execSQL("CREATE TABLE TODO(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TASK TEXT," +
                    "DUE TEXT," +
                    "DONE INTEGER);");

            // 关联的表
            db.execSQL("CREATE TABLE TODO_LINK(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TODO INTEGER," +
                    "LINK INTEGER," +
                    "UNIQUE(TODO, LINK))");
            /* 加入示例数据，注意这里不能使用与在MainActivity中一样，通过mDataBaseHelper添加示例
             * 原因是此时的GarnetDatabaseHelper尚未完成初始化（没有Context）
             * 只能通过使用OnUpgrade()方法中传入的db变量进行数据库操作*/
            insertSampleTodo(db, "学习高数", "2024-07-19");
            insertSampleTodo(db, "练习吉他", "2024-07-23");
            insertSampleTodo(db, "训练口语", "2024-09-16");
            insertSampleTodo(db, "英语听说", "2024-09-16");

            insertSampleInfoGroup(db, "高等数学");
            insertSampleInfo(db, "高数链接1", 1);
            insertSampleInfo(db, "高数链接2", 1);
            insertSampleInfo(db, "高数链接3", 1);

            insertSampleInfoGroup(db, "Android开发");
            insertSampleInfo(db, "RecyclerView", 2);
            insertSampleInfo(db, "CheckBox", 2);
            insertSampleInfo(db, "TextView", 2);

            insertSampleInfoGroup(db, "程序设计");
            insertSampleInfo(db, "C语言", 3);
            insertSampleInfo(db, "Python", 3);
            insertSampleInfo(db, "Java", 3);
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

    private void insertSampleTodo(SQLiteDatabase db, String todoTask, String dueDate) {
        ContentValues c = new ContentValues();
        c.put("TASK", todoTask);
        c.put("DUE", dueDate);
        c.put("DONE", 0);
        db.insert(TABLE_TODO, null, c);
    }

    public InfoItem insertInfo(InfoItem item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) { // 使用这样的try-with-resources可以自动调用close()方法，不用自己手动db.close()
            ContentValues contentValues = new ContentValues();
            contentValues.put("URI", item.getUri());
            contentValues.put("BELONG", item.getBelong());
            contentValues.put("DISPLAY", item.getDisplayString());
            long id = db.insert(TABLE_LINK, null, contentValues);
            item.setId(id);
            return item;
        }
    }

    private void insertSampleInfo(SQLiteDatabase db, String uri, long belong) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("URI", uri);
        contentValues.put("BELONG", belong);
        contentValues.put("DISPLAY", uri);//样例数据直接把uri设置成展示字符串，反正给的也是无效链接
        db.insert(TABLE_LINK, null, contentValues);
    }

    public InfoGroup insertInfoGroup(InfoGroup infoGroup) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", infoGroup.getName());
            long id = db.insert(TABLE_TITLE, null, contentValues);
            return new InfoGroup(infoGroup.getName(), id);
        }
    }

    private void insertSampleInfoGroup(SQLiteDatabase db, String infoGroupName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", infoGroupName);
        db.insert(TABLE_TITLE, null, contentValues);
    }

    /**
     * 将资料-待办关联添加到表中。如果所要插入的值已经存在就跳过
     *
     * @param todoId         对应的{@link TodoItem}的id
     * @param infoItemIdList 所要与之关联的所有{@link InfoItem}的id
     */
    public void updateAttachment(long todoId, List<Long> infoItemIdList, List<Long> attachedIdList) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            for(long infoItemId : infoItemIdList) {
                db.delete(TABLE_TODO_LINK,"TODO = ? AND LINK = ?"
                        ,new String[]{String.valueOf(todoId), String.valueOf(infoItemId)});
            }// TODO: 2024-07-26 改了一下，是info和todo双检索
            for (long infoItemId : attachedIdList) {
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_TODO_LINK + "(TODO, LINK)" + " VALUES " +
                        "(" + todoId + "," + infoItemId + ")");//要求不能重复
            }
        }
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
/**获得以YYYY-MM-DD格式指定字符串对应日期中，所有未完成待办构成的字符串，以'\n'分隔*/
    public String loadTodoString(final Date date) {
        List<String> taskFoundList = new ArrayList<>(5);//预计5个差不多，一天不会有那么多待办
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        Log.d("NOTI", "Loading todo at date:" + dateString);
        try (SQLiteDatabase db = this.getWritableDatabase()){
            Cursor cursor = db.query(TABLE_TODO,
                    new String[]{"TASK","DONE"},
                    "DUE = ? AND DONE = ?",
                    new String[]{dateString, "0"},null, null, null);
            if (cursor.moveToFirst()){
                do {
                    String taskFound = cursor.getString(0);
                    taskFoundList.add(taskFound);
                }while(cursor.moveToNext());
            }
            if (taskFoundList.isEmpty())return null;
            else {
                return String.join("\n", taskFoundList);
            }
        }
    }


    public List<InfoItem> loadInfo(long infoGroupId) {
        List<InfoItem> ret = new ArrayList<>();
        String infoGroupIdString = String.valueOf(infoGroupId);
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query("LINK",
                    new String[]{"_id", "URI", "BELONG", "DISPLAY"},
                    "BELONG = ?", new String[]{infoGroupIdString}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    //符合BELONG的话，存进来
                    String uriFound = cursor.getString(1);
                    long idFound = cursor.getLong(0);
                    String displayFound = cursor.getString(3);

                    ret.add(new InfoItem(uriFound, infoGroupId, idFound, displayFound));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return ret;
        }
    }

    /**
     * 读取所有的info
     */
    public List<InfoItem> loadInfo() {
        List<InfoItem> ret = new ArrayList<>();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query("LINK",
                    new String[]{"_id", "URI", "BELONG", "DISPLAY"},
                    null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    String uriFound = cursor.getString(1);
                    long idFound = cursor.getLong(0);
                    String displayFound = cursor.getString(3);
                    long belongIdFound = cursor.getLong(2);

                    ret.add(new InfoItem(uriFound, belongIdFound, idFound, displayFound));
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

    public void deleteInfoGroup(InfoGroup infoGroup) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(infoGroup.getId());
            // 删除InfoGroup
            db.delete(TABLE_TITLE, "_id=?", new String[]{idString});

            // 删除InfoGroup下的所有链接（这方法里包含了删除链接和待办事项的关联
            List<InfoItem> infoItemsToDelete = loadInfo(infoGroup.getId());
            deleteInfo(infoItemsToDelete);
        }
    }

    public void deleteInfo(List<InfoItem> infoItemList){
        for (InfoItem item : infoItemList){
            deleteInfo(item);
        }
    }

    public void deleteInfo(InfoItem item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(item.getId());
            db.delete(TABLE_LINK, "_id=?", new String[]{idString});
            db.delete(TABLE_TODO_LINK,"LINK=?", new String[]{idString});
        }
    }

    public void updateTodoStatus(TodoItem ti, CheckBox cb) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            long id = ti.getId();
            String newIsCheckedString = cb.isChecked() ? "1" : "0";
            String idString = String.valueOf(id);
            ContentValues contentValues = new ContentValues();
            contentValues.put("DONE", newIsCheckedString);
            db.update(TABLE_TODO, contentValues, "_id = ?", new String[]{idString});
        }
    }

    public InfoGroup updateInfoGroup(InfoGroup group, String newTitle) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(group.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", newTitle);
            db.update(TABLE_TITLE, contentValues, "_id = ?", new String[]{idString});
            return new InfoGroup(newTitle, group.getId());
        }
    }

    /**
     * 查看传入列表里的{@link InfoItem}是否与相应的{@link TodoItem}相关
     */
    public List<Boolean> checkAttached(long todoItemId, List<InfoItem> infoItemList) {
        List<Boolean> ret = new ArrayList<>();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            for (InfoItem infoItem : infoItemList) {
                String selection = "TODO=? AND LINK=?";
                String[] selectionArgs = {String.valueOf(todoItemId), String.valueOf(infoItem.getId())};
                Cursor cursor = db.query(TABLE_TODO_LINK, new String[]{"_id"},
                        selection, selectionArgs, null, null, null);
                ret.add(cursor.moveToFirst());
                cursor.close();
            }
        }
        return ret;
    }

    /**
     * 查看传入的InfoGroup中，是否存在InfoItem与传入的TodoItem关联
     * @param todoItemId
     * @param infoGroup
     * @return 如果关联，则返回true
     */
    public boolean checkAttached(long todoItemId, InfoGroup infoGroup){
        List<Boolean> list = checkAttached(todoItemId, loadInfo(infoGroup.getId()));
        boolean isAllAttached = list.stream().anyMatch(b -> b);
        return isAllAttached;
    }



    public void updateInfoItem(InfoItem infoItem, String newDisplayString) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(infoItem.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("DISPLAY", newDisplayString);
            db.update(TABLE_LINK, contentValues, "_id = ?", new String[]{idString});
        }
    }

}
