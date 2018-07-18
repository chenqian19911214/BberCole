package com.bber.company.android.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bber_db";
    private static final int DB_VERSION = 2;
    private static DBHelper mInstance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    public synchronized static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //获取sql语句
        String sqlTableMsg = DBcolumns.CreateTableSQLFactory.createTableMsg();
        String sqlSession = DBcolumns.CreateTableSQLFactory.createSessionTable();
        String sqlTableFavorites = DBcolumns.CreateTableSQLFactory.createTableFavorites();
        String sqlMatchHistory = DBcolumns.CreateTableSQLFactory.createTableMatchHistory();
        String sqlBroadcastMsg = DBcolumns.CreateTableSQLFactory.createBoasdcastTableMsg();

        db.execSQL(sqlTableMsg);
        db.execSQL(sqlTableFavorites);
        db.execSQL(sqlMatchHistory);
        db.execSQL(sqlSession);
        db.execSQL(sqlBroadcastMsg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            String sqlTableMsg1 = "DROP TABLE IF EXISTS " + DBcolumns.TABLE_MSG;
            db.execSQL(sqlTableMsg1);
            String sqlSession = "DROP TABLE IF EXISTS " + DBcolumns.TABLE_SESSION;
            db.execSQL(sqlSession);

            onCreate(db);
        }
    }


}
