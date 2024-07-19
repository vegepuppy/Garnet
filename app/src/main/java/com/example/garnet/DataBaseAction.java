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

    private static final String TABLE_TITLE = "TITLE"; // TODO: 2024-07-19 只有这一个表用了常量，其他的需要弄吗

    //私有构造函数避免外部创建实例
    private static SQLiteDatabase db = null; // 数据库

    /** 在MainActivity中调用这方法进行初始化，利用SQLiteOpenHelper构造单例
     * 进行所有数据库操作之前必须调用init()方法*/
    public static void init(Context context) {
        if (db == null) {
            GarnetDatabaseHelper dbHelper = new GarnetDatabaseHelper(context);
            db = dbHelper.getWritableDatabase();
        }
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

        // TODO: 2024-07-19  这个document中提到，mainList是List<TodoGroup>类型对象，但实际上似乎不是
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
                    String titleFound = cursor.getString(1); // TODO: 2024-07-19 这里是Info的内容，真的叫Title吗
                    long idFound = cursor.getLong(0);

                    ret.add(new InfoItem(titleFound, infoGroupName, idFound));
                }while(cursor.moveToNext());
            }
            cursor.close();
            return ret;
        }

        /**
         * 将所有的Title读入InfoFragment的mainList
         * @return 返回所以在数据库中读到的Title构成的InfoGroup列表
         */
        public static List<InfoGroup> loadInfoGroup(){
            List<InfoGroup> ret = new ArrayList<>();
            Cursor cursor = db.query(TABLE_TITLE,
                    new String[]{"_id","NAME"},
                    null,null,null,null,null);

            if(cursor.moveToFirst()){
                do{
                    final int idIdx = 0;
                    final int titleIdx = 1;

                    long idFound = cursor.getLong(idIdx);
                    String titleFound = cursor.getString(titleIdx);

                    InfoGroup infoGroup = new InfoGroup(titleFound,idFound);

                    ret.add(infoGroup);
                }while ((cursor.moveToNext()));
            }
            return ret;
        }
    }

    public static class Insert{

        public static InfoGroup insertInfoGroup(InfoGroup infoGroup) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", infoGroup.getName());
            long id = db.insert(TABLE_TITLE,null,contentValues);// TODO: 2024-07-19 这些应该都改成常量，表的名字可能会改的
            return new InfoGroup(infoGroup.getName(), id);
        }

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
            db.delete("LINK","_id=?",new String[]{idString});
            Log.e("TAG", "Link id "+idString+" delete");
            // TODO: 2024-07-18 不应该execSQL
        }

        public static void deleteInfoGroup(InfoGroup group){
            String idString = String.valueOf(group.getId());
            db.delete("TITLE","_id=?",new String[]{idString});
            Log.e("TAG", "Title id "+idString+" delete");
        }
    }

    public static class Update{
        /**@deprecated 不需要修改uri的功能*/
        @Deprecated//确定不用修改Uri了
        public static InfoItem updateInfoURI(InfoItem item, String newUri){
            String idString = String.valueOf(item.getId());
            db.execSQL("UPDATE LINK SET URI = ? WHERE _id = ?",
                    new String[]{newUri,idString});
            Log.e("UPDATING DATABASE", item.getUri()+"   "+newUri);
            return new InfoItem(newUri, item.getBelong(), item.id); // TODO: 2024-07-19 这里直接访问了id？是否需要private
        }

        /**
         * 对指定的InfoGroup，修改其在数据库中的title
         * @param group 传入要修改的InfoGroup
         * @param newTitle 传入修改目标值
         * @return 返回修改好的InfoGroup
         */
        public static InfoGroup updateInfoTitle(InfoGroup group, String newTitle){
            String idString = String.valueOf(group.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", newTitle);
            db.update("TITLE",contentValues,"id = ?", new String[]{idString});
            Log.e("UPDATING DATABASE", group.getName()+"   "+newTitle);
            return new InfoGroup(newTitle, group.getId());
        }

        // 设置TodoItem是否完成了，并存储到数据库里
        public static void updateTodoStatus(TodoItem ti) {
            long id = ti.getId();
            String newIsCheckedString = ti.getCheckBox().isChecked() ? "1" : "0";
            String idString = String.valueOf(id);
            ContentValues contentValues = new ContentValues();
            contentValues.put("DONE", newIsCheckedString);
            db.update("TODO",contentValues,"id = ?", new String[]{idString});
        }
    }
}

