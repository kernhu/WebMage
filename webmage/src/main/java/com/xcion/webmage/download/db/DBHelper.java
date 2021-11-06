package com.xcion.webmage.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/10 12:54
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/10 12:54
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_TASK_INFO = "create table if not exists "
            + ConfigHelp.DB_TABLE_TASK
            + "("
            + "_id integer PRIMARY KEY autoincrement,"
            + "url text,"
            + "filename text,"
            + "mimetype text,"
            + "filepath text,"
            + "length integer,"
            + "state integer,"
            + "result text"
            + ")";

    private static final String SQL_CREATE_THREAD_INFO = "create table if not exists "
            + ConfigHelp.DB_TABLE_THREAD
            + "("
            + "_id integer PRIMARY KEY autoincrement,"
            + "thread_id integer,"
            + "url text,"
            + "start integer,"
            + "ends integer,"
            + "current integer,"
            + "state integer"
            + ")";

    private static final String SQL_DROP_TASK_INFO = "drop table if exists " + ConfigHelp.DB_TABLE_TASK;
    private static final String SQL_DROP_THREAD_INFO = "drop table if exists " + ConfigHelp.DB_TABLE_THREAD;


    public DBHelper(Context context) {
        this(context, ConfigHelp.DB_NAME, null, ConfigHelp.DB_VERSION);
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK_INFO);
        db.execSQL(SQL_CREATE_THREAD_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //先删除再创建
        db.execSQL(SQL_DROP_TASK_INFO);
        db.execSQL(SQL_CREATE_TASK_INFO);
        //先删除再创建
        db.execSQL(SQL_DROP_THREAD_INFO);
        db.execSQL(SQL_CREATE_THREAD_INFO);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
