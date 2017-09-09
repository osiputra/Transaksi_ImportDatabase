package com.aplikasipasardemo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String TAG = "SQLite Helper";

    public static final String DATABASE_NAME = "ProductDB";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase mDataBase;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Start SQL Transaction
    public void insertDataTrans(String name, String qty, String price){

        String sql = "INSERT INTO PRODUCTTEMP VALUES (NULL, '"+name+"', '"+qty+"', '"+price+"')";
        mDataBase.execSQL(sql);
    }

    public void updateDataTrans(String name, String qty, String price, String id){

        String sql = "UPDATE PRODUCTTEMP SET " +
                "name = '"+name+"', " +
                "qty = '"+qty+"', " +
                "price = '"+price+"' " +
                "WHERE Id = '"+id+"'";
        mDataBase.execSQL(sql);
    }



    public void deleteDataTrans(String id){

        String sql = "DELETE FROM PRODUCTTEMP WHERE Id = '"+id+"'";
        mDataBase.execSQL(sql);
    }


    public void deleteAllRecordTrans(){

        String sql = "DELETE FROM PRODUCTTEMP";
        mDataBase.execSQL(sql);
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    //  End SQL Transaction

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "unit VARCHAR, " +
                "price VARCHAR, " +
                "image BLOB)";

        Log.i(TAG, "onCreate: "+sql);
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS PRODUCTTEMP" +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "qty VARCHAR, " +
                "price VARCHAR)";

        Log.i(TAG, "onCreate: "+sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean checkDB(){

        File sd = Environment.getExternalStorageDirectory();
        String backupDBPath = DATABASE_NAME;

        File backupDB = new File(sd, backupDBPath);

        if (!backupDB.exists())
            return false;

        return true;
    }

    public boolean importDB() throws SQLException{

        File sd = Environment.getExternalStorageDirectory();
        String backupDBPath = DATABASE_NAME;

        File backupDB = new File(sd, backupDBPath);

        mDataBase = SQLiteDatabase.openDatabase(backupDB.getPath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);

        return mDataBase != null;
    }

}
