package com.xcion.webmage.download.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xcion.webmage.download.db.ConfigHelp;
import com.xcion.webmage.download.db.DBHelper;
import com.xcion.webmage.download.db.DatabaseManager;
import com.xcion.webmage.download.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 16:48
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 16:48
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class TaskDaoImpl implements TaskDao {

    private Context context;
    private DBHelper mHelper = null;

    public TaskDaoImpl(Context context) {
        this.context = context;
        mHelper = new DBHelper(context);
    }

    @Override
    public void insertTask(FileInfo info) {
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", info.getUrl());
        values.put("filename", info.getFileName());
        values.put("mimetype", info.getMimeType());
        values.put("filepath", info.getFilePath());
        values.put("length", info.getLength());
        values.put("state", info.getState());
        values.put("result", info.getResult());
        db.insert(ConfigHelp.DB_TABLE_TASK, null, values);
        mDatabaseManager.closeDatabase();
    }

    @Override
    public void deleteTask(String url) {
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        db.delete(ConfigHelp.DB_TABLE_TASK, "url?", new String[]{url});
        mDatabaseManager.closeDatabase();
    }

    @Override
    public void updateTask(String url, FileInfo info) {
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("filename", info.getFileName());
        values.put("mimetype", info.getMimeType());
        values.put("filepath", info.getFilePath());
        values.put("length", info.getLength());
        values.put("state", info.getState());
        values.put("result", info.getResult());
        db.update(ConfigHelp.DB_TABLE_TASK, values, "url=?", new String[]{url});
        mDatabaseManager.closeDatabase();
    }

    @Override
    public FileInfo getTaskByUrl(String url) {
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ConfigHelp.DB_TABLE_TASK + " where url = ?", new String[]{url});
        FileInfo info = null;
        while (cursor.moveToNext()) {
            info = new FileInfo();
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
            info.setMimeType(cursor.getString(cursor.getColumnIndex("mimetype")));
            info.setFilePath(cursor.getString(cursor.getColumnIndex("filepath")));
            info.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            info.setState(cursor.getInt(cursor.getColumnIndex("state")));
            info.setResult(cursor.getString(cursor.getColumnIndex("result")));
        }
        cursor.close();
        mDatabaseManager.closeDatabase();
        return info;
    }

    @Override
    public List<FileInfo> getAllTasks() {
        List<FileInfo> list = new ArrayList<>();
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ConfigHelp.DB_TABLE_TASK, new String[]{});
        FileInfo info = null;
        while (cursor.moveToNext()) {
            info = new FileInfo();
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
            info.setMimeType(cursor.getString(cursor.getColumnIndex("mimetype")));
            info.setFilePath(cursor.getString(cursor.getColumnIndex("filepath")));
            info.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            info.setState(cursor.getInt(cursor.getColumnIndex("state")));
            info.setResult(cursor.getString(cursor.getColumnIndex("result")));
            list.add(info);
        }
        cursor.close();
        mDatabaseManager.closeDatabase();
        return list;
    }

    @Override
    public List<FileInfo> getAllTasksBy(int state) {
        List<FileInfo> list = new ArrayList<>();
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ConfigHelp.DB_TABLE_TASK + " where state = ?", new String[]{String.valueOf(state)});
        FileInfo info = null;
        while (cursor.moveToNext()) {
            info = new FileInfo();
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
            info.setMimeType(cursor.getString(cursor.getColumnIndex("mimetype")));
            info.setFilePath(cursor.getString(cursor.getColumnIndex("filepath")));
            info.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            info.setState(cursor.getInt(cursor.getColumnIndex("state")));
            info.setResult(cursor.getString(cursor.getColumnIndex("result")));
            list.add(info);
        }
        cursor.close();
        mDatabaseManager.closeDatabase();
        return list;
    }


    @Override
    public boolean isExists(String url) {
        DatabaseManager mDatabaseManager = DatabaseManager.getInstance(mHelper);
        SQLiteDatabase db = mDatabaseManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ConfigHelp.DB_TABLE_TASK + " where url = ?",
                new String[]{url});
        while (cursor.moveToNext()) {
            cursor.close();
            mDatabaseManager.closeDatabase();
            return true;
        }
        return false;
    }
}
