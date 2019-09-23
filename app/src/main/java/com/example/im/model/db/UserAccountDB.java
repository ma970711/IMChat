package com.example.im.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.im.model.dao.UserAccountTable;

public class UserAccountDB extends SQLiteOpenHelper {
    //构造方法
    public UserAccountDB(Context context) {
        super(context, "account.db", null, 1);
    }
    //数据库创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库表
        db.execSQL(UserAccountTable.CREATE_TAB);
    }
    //数据库更新的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
