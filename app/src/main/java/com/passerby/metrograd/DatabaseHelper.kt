package com.passerby.metrograd

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.http.SslCertificate
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.sql.SQLException

class DatabaseHelper internal constructor(private val myContext: Context):
    SQLiteOpenHelper(myContext, DB_NAME, null, SCHEMA){
    override fun onCreate(p0: SQLiteDatabase?) {}
    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
    fun createDB(){
        var myInput: InputStream? = null
        var myOutput: OutputStream? = null
        try {
            val file = File(DB_PATH)
            if (!file.exists()){
                myInput = myContext.assets.open(DB_NAME)
                val outFileName = DB_PATH
                myOutput = FileOutputStream(outFileName)
                val buffer = ByteArray(1024)
                var length: Int
                while (myInput.read(buffer).also{ length = it } > 0){
                    myOutput.write(buffer, 0, length)
                }
                myOutput.flush()
            }
        } catch (ex: Exception){
            Log.d("DatabaseHelper", ex.message!!)
        } finally {
            try {
                myOutput?.close()
                myInput?.close()
            } catch (ex: Exception){
                Log.d("DatabaseHelper", ex.message!!)
            }
        }
    }

    @Throws(SQLException::class)
    fun open(): SQLiteDatabase{
        return SQLiteDatabase.openDatabase(DB_PATH,null, SQLiteDatabase.OPEN_READWRITE)
    }

    companion object{
        private lateinit var DB_PATH: String
        private const val DB_NAME = "metrograd5.db"
        private const val SCHEMA = 1
        const val TABLE_U = "users"
        const val TABLE_M = "monitors_ready"
        const val TABLE_G = "goods"

        //table users
        const val COLUMN_ID_U = "_id"
        const val COLUMN_SURNAME_U = "surname"
        const val COLUMN_NAME_U = "name"
        const val COLUMN_PATR_U = "patronymic"
        const val COLUMN_PHONE_U = "p_number"
        const val COLUMN_PASS_U = "password"
        const val COLUMN_EMAIL_U = "email"
        const val COLUMN_STATUS_U = "status"

        //table monitors
        const val COLUMN_ID_M = "_id"
        const val COLUMN_LINK_M = "link"
        const val COLUMN_NAME_M = "name"

        //table goods
        const val COLUMN_ID_G = "_id"
        const val COLUMN_NAME_G = "name"
    }

    init {
        DB_PATH = myContext.filesDir.path + DB_NAME
    }
}