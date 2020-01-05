package com.xxun.watch.xunchatroom.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class BaseDB{
    protected Context mContext = null;
    private String  mTableName = null;

    public BaseDB(Context mContext,String mTableName) {
        super();
        this.mContext = mContext;
        this.mTableName = mTableName;
    }

    public BaseDB(Context mContext) {
        super();
        this.mContext = mContext;
    }

    protected void closeCursor(Cursor cursor)
    {
        try
        {
            cursor.close();
        }
        catch(Exception e)
        {}
    }
    protected String getTableName()
    {
        return this.mTableName;
    }

    protected SQLiteDatabase openWritableDb()
    {


        MyDBOpenHelper dbHelper = new MyDBOpenHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db == null)
        {

        }

        return db;
    }

    protected SQLiteDatabase openReadableDb()
    {

        MyDBOpenHelper dbHelper = new MyDBOpenHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db == null)
        {

        }
        return db;
    }

    /**
     * åˆ é™¤idå¯¹åº”çš„è¡¨é¡¹
     * @param id
     * @return è¿”å›žè¢«åˆ é™¤çš„è®°å½•æ¡æ•°
     */
    public int delete(long id)
    {
        SQLiteDatabase db;
        db = this.openWritableDb();
        try {
            if (db == null)
                return 0;

            int rows = db.delete(mTableName, "_id=?", new String[]{
                    String.valueOf(id)
            });

            db.close();



            return rows;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * åˆ é™¤è¡¨ä¸­æ‰€æœ‰è®°å½•
     * @return è¿”å›žè¢«åˆ é™¤çš„è®°å½•æ¡æ•°
     */
    public int deleteAll()
    {
        SQLiteDatabase db;
        db = this.openWritableDb();

        if (db == null)
            return 0;

        int affectRows = db.delete(mTableName, null, null);

        db.close();



        return affectRows;
    }
}

