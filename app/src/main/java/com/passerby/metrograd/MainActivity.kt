package com.passerby.metrograd

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var sqlHelper: DatabaseHelper? = null
    var db: SQLiteDatabase? = null
    lateinit var userId: IntArray
    lateinit var userStatus: IntArray
    var columnIndexId: Int = 0
    var columnIndexStatus: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        sqlHelper = DatabaseHelper(applicationContext)
        sqlHelper!!.createDB()

        reg_txt.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        db = sqlHelper!!.open()
    }

    fun logIntent(view: View) {
        val phone = pnEditText.unMasked
        val pass = passEditText.text.toString()

        if (phone.isNotEmpty() || pass.isNotEmpty()) {
            val idCursor = db!!.rawQuery(
                " select * from " + DatabaseHelper.TABLE_U + " where " +
                        DatabaseHelper.COLUMN_PHONE_U + " = " + "'" + phone + "'", null
            )

            columnIndexId = idCursor.getColumnIndex("_id")
            userId = IntArray(idCursor.count)
            if (idCursor.moveToFirst()) {
                for (i in 0 until idCursor.count) {
                    userId[i] = idCursor.getInt(columnIndexId)
                    idCursor.moveToNext()
                }
            }

            var phoneCursor = db!!.rawQuery(
                " select * from " + DatabaseHelper.TABLE_U + " where " +
                        DatabaseHelper.COLUMN_PHONE_U + " = " + "'" + phone + "'", null
            )

            var passCursor = db!!.rawQuery(
                " select * from " + DatabaseHelper.TABLE_U + " where " +
                        DatabaseHelper.COLUMN_PASS_U + " = " + "'" + pass + "'", null
            )

            var phoneCount = phoneCursor.count
            var passCount = passCursor.count

            if (phoneCount == 0) {
                textInputLayout_pnum.hint = "Неправильный номер"
            } else {
                textInputLayout_pnum.hint = "Введите номер телефона"
            }
            if (passCount == 0) {
                textInputLayout_pass.hint = "Неправильный пароль"
            } else {
                textInputLayout_pass.hint = "Введите пароль"
                if (phoneCount > 0 && passCount > 0) {
                    var statusCursor = db!!.rawQuery(
                        " select " + DatabaseHelper.COLUMN_STATUS_U + " from " +
                                DatabaseHelper.TABLE_U + " where " +
                                DatabaseHelper.COLUMN_ID_U + " = " + "'" +
                                userId[0].toString() + "'", null
                    )
                    columnIndexStatus = statusCursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS_U)
                    userStatus = IntArray(statusCursor.count)
                    if (statusCursor.moveToFirst()) {
                        for (i in 0 until statusCursor.count) {
                            userStatus[i] = statusCursor.getInt(columnIndexStatus)
                            statusCursor.moveToNext()
                        }
                    }
                    val intent = Intent(this, ReadyMonitorActivity::class.java)
                    intent.putExtra("userStatus", userStatus[0].toString())
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show()
        }
    }
}