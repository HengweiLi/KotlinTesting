package com.example.kotlintesting.DBUtils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(
    context: Context?, name: String?, factory: CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {



    private var mDataBase: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table forecast(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "wdate varchar(30))"
        )
        Log.v("mytab", "-->onCreate")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        Log.v("mytab", "-->onOpen")
    }

    @Synchronized
    override fun close() {
        super.close()
    }

    companion object {
        private const val Forecast_TABLE = "forecast"
    }

    init {
        mDataBase = this.writableDatabase
    }
}