package com.example.garnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseAction {
    private DataBaseAction() {}

    //私有构造函数避免外部创建实例
    private static SQLiteDatabase db = null; // 数据库

    // 在MainActivity中调用这方法进行初始化，利用SQLiteOpenHelper构造单例
    public static void init(Context context) {
        if (db == null) {
            GarnetDatabaseHelper dbHelper = new GarnetDatabaseHelper(context);
            db = dbHelper.getWritableDatabase();
        }
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

    /**
     * 关闭应用数据库
     */
    public static void close(){
        db.close();
    }

    /** 所有读取数据的函数*/
    public static class Load{
        /** 将所有读到的TodoItem读入mainList(一个List<TodoGroup>类型对象)
         * 返回所有在数据库中读到的的TodoItem构成的列表*/
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

        /** 将所有读到的TodoItem读入mainList(一个List<TodoGroup>类型对象)
         * @param infoGroupName 在这一InfoGroup种进行查找
         * @return 返回所有在数据库中读到的InfoItem构成的列表*/
        public static List<InfoItem> loadInfo(String infoGroupName){
            List<InfoItem> ret = new ArrayList<>();

            Cursor cursor = db.query("LINK",
                    new String[]{"_id","URI","BELONG"},
                    "BELONG = ?",new String[]{infoGroupName},null,null,null);

            if(cursor.moveToFirst()){
                do{
                    //符合BELONG的话，存进来
                    String titleFound = cursor.getString(1);
                    long idFound = cursor.getLong(0);
                    // TODO: 2024-07-18 用常量
                    ret.add(new InfoItem(titleFound, infoGroupName, idFound));
                }while(cursor.moveToNext());
            }
            cursor.close();
            return ret;
        }
    }

    public static class Insert{
        /**将一个未知id的{@link TodoItem}写入数据库
         * @param item 这个未知id的{@link  TodoItem}
         * @return {@link TodoItem}加上id后的传入参数
         * */
        public static TodoItem insertTodo(TodoItem item){
            ContentValues c = new ContentValues();
            c.put("DUE",item.getDueDate());
            c.put("DONE",item.isDone());
            c.put("TASK",item.getTask());

            // insert()方法返回的就是被插入地方的_id
            long id = db.insert("TODO", null, c);
            return new TodoItem(item.getTask(), item.getDueDate(), id, item.isDone());
        }

        /**将一个未知id的{@link InfoItem}写入数据库
         * @param item 这个未知id的{@link  InfoItem}
         * @return {@link InfoItem}加上id后的传入参数
         * */
        public static InfoItem insertInfo(InfoItem item){
            ContentValues contentValues = new ContentValues();
            contentValues.put("URI",item.getUri());
            contentValues.put("BELONG",item.getBelong());
            long id = db.insert("LINK",null,contentValues);
            return new InfoItem(item.getUri(),item.getBelong(),id);
        }
    }
    public static class Delete{
        public static void deleteInfo(InfoItem item){
            String idString = String.valueOf(item.getId());
            db.execSQL("DELETE FROM LINK WHERE _id = ?",new String[]{idString});
            Log.e("TAG", "Link id "+idString+" delete");
            // TODO: 2024-07-18 不应该execSQL
        }
    }

    public static class Update{
        public static InfoItem updateInfoURI(InfoItem item, String newUri){
            String idString = String.valueOf(item.getId());
            db.execSQL("UPDATE LINK SET URI = ? WHERE _id = ?",
                    new String[]{newUri,idString});
            Log.e("UPDATING DATABASE", item.getUri()+"   "+newUri);
            return new InfoItem(newUri, item.getBelong(), item.id);
        }
    }
}

