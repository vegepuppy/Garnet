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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class GarnetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "garnetDatabase.db";// 须加上扩展名
    private static final int DB_VERSION = 1;
    private static final String TABLE_INFO_GROUP = "INFO_GROUP";
    private static final String TABLE_INFO_ITEM = "INFO_ITEM";
    private static final String TABLE_TODO = "TODO";
    private static final String TABLE_TODO_INFO_ITEM = "TODO_INFO_ITEM";

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
    private final String formattedDate = formatter.format(calendar.getTime());

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
            db.execSQL("CREATE TABLE "+ TABLE_INFO_GROUP +" (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT);");

            // 信息条目的链接
            db.execSQL("CREATE TABLE "+ TABLE_INFO_ITEM +" (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "CONTENT TEXT," +
                    "BELONG INTEGER," +
                    "DISPLAY TEXT," +
                    "TYPE INTEGER);");

            // 待办事项的相关信息：名称、日期、是否已经完成（0或1）
            db.execSQL("CREATE TABLE "+TABLE_TODO+"(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TASK TEXT," +
                    "DUE TEXT," +
                    "DONE INTEGER);");

            // 关联的表
            db.execSQL("CREATE TABLE "+TABLE_TODO_INFO_ITEM+"(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TODO INTEGER," +
                    "INFO_ITEM INTEGER," +
                    "UNIQUE(TODO, INFO_ITEM))");
            /* 加入示例数据，注意这里不能使用与在MainActivity中一样，通过mDataBaseHelper添加示例
             * 原因是此时的GarnetDatabaseHelper尚未完成初始化（没有Context）
             * 只能通过使用OnUpgrade()方法中传入的db变量进行数据库操作*/
            insertSampleTodo(db, "学习高数", "2024-07-19");
            insertSampleTodo(db, "练习吉他", "2024-07-23");
            insertSampleTodo(db, "训练口语", "2024-09-16");
            insertSampleTodo(db, "英语听说", "2024-09-16");

            insertSampleInfoGroup(db, "高等数学");
            insertSampleLinkInfoItem(db, "高数链接1", 1);
            insertSampleLinkInfoItem(db, "高数链接2", 1);
            insertSampleLinkInfoItem(db, "高数链接3", 1);
            insertSampleNoteInfoItem(db, "高数笔记1", "y = x+b",1);

            insertSampleInfoGroup(db, "Android开发");
            insertSampleLinkInfoItem(db, "RecyclerView", 2);
            insertSampleLinkInfoItem(db, "CheckBox", 2);
            insertSampleLinkInfoItem(db, "TextView", 2);

            insertSampleInfoGroup(db, "程序设计");
            insertSampleLinkInfoItem(db, "C语言", 3);
            insertSampleLinkInfoItem(db, "Python", 3);
            insertSampleLinkInfoItem(db, "Java", 3);
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

    public InfoItem insertInfoItem(InfoItem item) {
        try (SQLiteDatabase db = this.getWritableDatabase()) { // 使用这样的try-with-resources可以自动调用close()方法，不用自己手动db.close()
            ContentValues contentValues = new ContentValues();
            contentValues.put("CONTENT", item.getContent());
            contentValues.put("BELONG", item.getBelong());
            contentValues.put("DISPLAY", item.getDisplayString());
            if (item instanceof LinkInfoItem) {
                contentValues.put("TYPE", InfoItem.TYPE_LINK);
            } else if (item instanceof NoteInfoItem) {
                contentValues.put("TYPE", InfoItem.TYPE_NOTE);
            }

            long id = db.insert(TABLE_INFO_ITEM, null, contentValues);
            item.setId(id);
            return item;
        }
    }

    private void insertSampleLinkInfoItem(SQLiteDatabase db, String uri, long belong) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("CONTENT", uri);
        contentValues.put("BELONG", belong);
        contentValues.put("DISPLAY", uri);//样例数据直接把uri设置成展示字符串，反正给的也是无效链接
        contentValues.put("TYPE", InfoItem.TYPE_LINK);
        db.insert(TABLE_INFO_ITEM, null, contentValues);
    }

    private void insertSampleNoteInfoItem(SQLiteDatabase db, String title, String content, long belong){
        ContentValues contentValues = new ContentValues();
        contentValues.put("CONTENT", content);
        contentValues.put("BELONG", belong);
        contentValues.put("DISPLAY", title);//样例数据直接把uri设置成展示字符串，反正给的也是无效链接
        contentValues.put("TYPE", InfoItem.TYPE_NOTE);
        db.insert(TABLE_INFO_ITEM, null, contentValues);
    }

    public InfoGroup insertInfoGroup(InfoGroup infoGroup) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", infoGroup.getName());
            long id = db.insert(TABLE_INFO_GROUP, null, contentValues);
            return new InfoGroup(infoGroup.getName(), id);
        }
    }

    private void insertSampleInfoGroup(SQLiteDatabase db, String infoGroupName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", infoGroupName);
        db.insert(TABLE_INFO_GROUP, null, contentValues);
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
                db.delete(TABLE_TODO_INFO_ITEM,"TODO = ? AND INFO_ITEM = ?"
                        ,new String[]{String.valueOf(todoId), String.valueOf(infoItemId)});
            }// TODO: 2024-07-26 改了一下，是info和todo双检索
            for (long infoItemId : attachedIdList) {
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_TODO_INFO_ITEM + "(TODO, INFO_ITEM)" + " VALUES " +
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

        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        return loadTodoString(dateString);
    }

    public String loadTodoString(String dateString){
        List<String> taskFoundList = new ArrayList<>(5);//预计5个差不多，一天不会有那么多待办

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
            cursor.close();
            if (taskFoundList.isEmpty())return null;
            else {
                return String.join("\n", taskFoundList);
            }
        }
    }

    //只能主页用
    public List<HomeItem> loadHome(){
        List<HomeItem> homelist = new ArrayList<>();
        String taskFound;
        long idFound;
        String linkFound;
        int uriId;
        boolean doneFound;
        String uriFound;
        List<String> tasklist = new ArrayList<>();
        List<Long> idlist = new ArrayList<>();
        List<Boolean> doneList = new ArrayList<>();
        //用来存todo的task对应的link的id值
        List<List<Integer>> todo_link = new ArrayList<>();
        //读TODO表
        try (SQLiteDatabase db1 = this.getWritableDatabase()){
            Cursor cursor = db1.query(TABLE_TODO,
                    new String[]{"_id","TASK","DONE"},
                    "DUE = ?",new String[]{"2024-09-16"},null,null,null);
            if(cursor.moveToFirst()){
                do{
                    final int idIdx = 0;
                    final int taskIdx = 1;
                    final int doneIdx = 2;

                    taskFound = cursor.getString(taskIdx);
                    idFound = cursor.getLong(idIdx);
                    doneFound = cursor.getInt(doneIdx) > 0;
                    tasklist.add(taskFound);
                    idlist.add(idFound);
                    doneList.add(doneFound);
                }while(cursor.moveToNext());
            }
            if (tasklist.isEmpty()){
                HomeItem HI = null;
                homelist.add(HI);
                return  homelist;
            }
            cursor.close();
        }
        //读TODO_LINK表，填写todo_link二维列表
        for (int i = 0; i < idlist.size(); i++) {
            List<Integer> row = new ArrayList<>();
            try (SQLiteDatabase db2 = this.getWritableDatabase()) {
                Cursor cursor = db2.query(TABLE_TODO_INFO_ITEM,
                        new String[]{"INFO_ITEM"},
                        "TODO = ?", new String[]{Long.toString(idlist.get(i))},
                        null, null, null);
                if (cursor.moveToFirst()) { // 检查是否有数据
                    do {
                        uriId = cursor.getInt(0);
                        row.add(uriId);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                // 只有当 row 不为空时才添加到 todo_link
                if (!row.isEmpty()) {
                    todo_link.add(row);
                } else {
                    todo_link.add(null);
                }
            }
        }
       //读LINK表，通过todo_link表得到的id值来获取link表中的uri
        for (int i = 0; i < idlist.size(); i++) {
            List<String> link = new ArrayList<>();
            List<String> uri = new ArrayList<>();
            List<Integer> linkId = (todo_link.get(i) != null) ? todo_link.get(i) : new ArrayList<>(); // 初始化 linkId 以避免 NullPointerException
            if (linkId.isEmpty()) {
                link.add("无链接");
                HomeItem hi = new HomeItem(tasklist.get(i),link,link,doneList.get(i),false,idlist.get(i));
                homelist.add(hi);
            } else {
                for (int cnt = 0; cnt < linkId.size(); cnt++) {
                    try (SQLiteDatabase db3 = this.getWritableDatabase()) {
                        Cursor cursor = db3.query(TABLE_INFO_ITEM,
                                new String[]{"DISPLAY","CONTENT"},
                                "_id = ?", new String[]{Integer.toString(linkId.get(cnt))},
                                null, null, null);
                        if (cursor.moveToFirst()) {
                            linkFound = cursor.getString(0);
                            uriFound = cursor.getString(1);
                            link.add(linkFound);
                            uri.add(uriFound);
                        }
                        cursor.close();
                    }
                }
                HomeItem hi = new HomeItem(tasklist.get(i),link,uri,doneList.get(i),false,idlist.get(i));
                homelist.add(hi);
            }
        }
       return homelist;
    }

    public List<InfoItem> loadInfo(long infoGroupId) {
        List<InfoItem> ret = new ArrayList<>();
        String infoGroupIdString = String.valueOf(infoGroupId);
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query(TABLE_INFO_ITEM,
                    new String[]{"_id", "CONTENT", "BELONG", "DISPLAY","TYPE"},
                    "BELONG = ?", new String[]{infoGroupIdString}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    //符合BELONG的话，存进来
                    String contentFound = cursor.getString(1);
                    long idFound = cursor.getLong(0);
                    String displayFound = cursor.getString(3);
                    int type = cursor.getInt(4);

                    if(type == InfoItem.TYPE_LINK){
                        ret.add(new LinkInfoItem(displayFound,contentFound, infoGroupId, idFound));
                    }else if(type == InfoItem.TYPE_NOTE){
                        ret.add(new NoteInfoItem(displayFound,contentFound, infoGroupId, idFound));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            return ret;
        }
    }

    public List<InfoGroup> loadInfoGroup() {
        List<InfoGroup> ret = new ArrayList<>();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.query(TABLE_INFO_GROUP,
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
            db.delete(TABLE_INFO_GROUP, "_id=?", new String[]{idString});

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
            db.delete(TABLE_INFO_ITEM, "_id=?", new String[]{idString});
            db.delete(TABLE_TODO_INFO_ITEM,"INFO_ITEM=?", new String[]{idString});
        }
    }

    /**删除所有已完成的待办事项*/
    public void deleteDone() {
        try (SQLiteDatabase db = this.getWritableDatabase()){
            db.delete(TABLE_TODO_INFO_ITEM, "TODO IN (SELECT _id FROM "+TABLE_TODO+" WHERE DONE = ?)", new String[]{"1"});
            db.delete(TABLE_TODO, "DONE = ?", new String[]{"1"});
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

    //只能主页用
    public void updateHomeStatus(HomeItem hi, CheckBox cb) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            long id = hi.getId();
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
            db.update(TABLE_INFO_GROUP, contentValues, "_id = ?", new String[]{idString});
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
                String selection = "TODO=? AND INFO_ITEM=?";
                String[] selectionArgs = {String.valueOf(todoItemId), String.valueOf(infoItem.getId())};
                Cursor cursor = db.query(TABLE_TODO_INFO_ITEM, new String[]{"_id"},
                        selection, selectionArgs, null, null, null);
                ret.add(cursor.moveToFirst());
                cursor.close();
            }
        }
        return ret;
    }


    public String countAttached(long todoItemId, long infoGroupId){
        List<Boolean> list = checkAttached(todoItemId, loadInfo(infoGroupId));
        long checked = list.stream().filter(b -> b).count(); //统计为true的数量
        long total = list.size();
        return "("+checked+"/"+total+")";
    }



    public void updateInfoItem(InfoItem infoItem, String displayString) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(infoItem.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("DISPLAY", displayString);
            db.update(TABLE_INFO_ITEM, contentValues, "_id = ?", new String[]{idString});
        }
    }

    public void updateInfoItem(InfoItem infoItem, String displayString, String content){
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String idString = String.valueOf(infoItem.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put("DISPLAY", displayString);
            contentValues.put("CONTENT", content);
            db.update(TABLE_INFO_ITEM, contentValues, "_id = ?", new String[]{idString});
        }
    }

    public void insertAttachment(TodoItem todoItem, InfoItem infoItem) {
        try(SQLiteDatabase db = this.getWritableDatabase()){
            ContentValues contentValues = new ContentValues();
            contentValues.put("TODO", String.valueOf(todoItem.getId()));
            contentValues.put("INFO_ITEM", String.valueOf(infoItem.getId()));
            db.insert(TABLE_TODO_INFO_ITEM, null, contentValues);
            Log.d("DATA", "insertAttachment: "+todoItem.getTask()+"<->"+infoItem.getDisplayString());
        }
    }

    public void deleteAttachment(TodoItem todoItem, InfoItem infoItem) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String todoIdString = String.valueOf(todoItem.getId());
            String infoIdString = String.valueOf(infoItem.getId());

            db.delete(TABLE_TODO_INFO_ITEM, "TODO = ? AND INFO_ITEM = ?",
                    new String[]{todoIdString, infoIdString});
            Log.d("DATA", "deleteAttachment: "+todoItem.getTask()+"<->"+infoItem.getDisplayString());

        }
    }
}
