package com.passerby.metrograd

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reg.*

class RegActivity : AppCompatActivity() {

    var sqlHelper: DatabaseHelper? = null
    var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        supportActionBar?.hide()

        sqlHelper = DatabaseHelper(this)
        sqlHelper!!.createDB()

        return_btn2.setOnClickListener {
            val logIntent = Intent(this, MainActivity::class.java)
            startActivity(logIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        db = sqlHelper!!.open()
    }

    fun regUser(view: View) {
        var phone = phone_reg.unMasked.toString().trim()
        var pass = pass_reg.text.toString().trim()
        var passConf = pass_conf_reg.text.toString().trim()
        var name = name_reg.text.toString().trim()
        var surname = surname_reg.toString().trim()

        if (pass != passConf) {
            pass_reg.setBackgroundResource(R.drawable.edittext_style_error)
            pass_conf_reg.setBackgroundResource(R.drawable.edittext_style_error)
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
        } else {
            pass_reg.setBackgroundResource(R.drawable.edittext_style)
            pass_conf_reg.setBackgroundResource(R.drawable.edittext_style)

            if (phone.isEmpty() || pass.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(
                    this, "Все поля должны быть заполнены",
                    Toast.LENGTH_SHORT
                ).show()
                phone_reg.setBackgroundResource(R.drawable.edittext_style_error)
                pass_reg.setBackgroundResource(R.drawable.edittext_style_error)
                pass_conf_reg.setBackgroundResource(R.drawable.edittext_style_error)
                name_reg.setBackgroundResource(R.drawable.edittext_style_error)
                surname_reg.setBackgroundResource(R.drawable.edittext_style_error)
            } else {
                phone_reg.setBackgroundResource(R.drawable.edittext_style)
                pass_reg.setBackgroundResource(R.drawable.edittext_style)
                pass_conf_reg.setBackgroundResource(R.drawable.edittext_style)
                name_reg.setBackgroundResource(R.drawable.edittext_style)
                surname_reg.setBackgroundResource(R.drawable.edittext_style)

                if (phone.length != 11 || pass.length < 5) {
                    pass_reg.setBackgroundResource(R.drawable.edittext_style_error)
                    pass_conf_reg.setBackgroundResource(R.drawable.edittext_style_error)

                    val toast = Toast.makeText(
                        this,
                        "Пароль должен быть не менее 5 символов", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    pass_reg.setBackgroundResource(R.drawable.edittext_style)
                    pass_conf_reg.setBackgroundResource(R.drawable.edittext_style)

                    val phoneCursor = db!!.rawQuery(
                        " select * from " + "'" + DatabaseHelper.TABLE_U + "'" + " where " +
                                DatabaseHelper.COLUMN_PHONE_U + " = " + "'" + phone + "'", null
                    )

                    if (phoneCursor.count > 0) {
                        phone_reg.setBackgroundResource(R.drawable.edittext_style_error)
                        Toast.makeText(
                            this,
                            "Пользователь с таким номером телефона уже существует",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        phone_reg.setBackgroundResource(R.drawable.edittext_style)

                        db!!.execSQL(
                            " insert into " + DatabaseHelper.TABLE_U + " ( " +
                                    DatabaseHelper.COLUMN_PHONE_U + " , " + DatabaseHelper.COLUMN_PASS_U + " , " +
                                    DatabaseHelper.COLUMN_NAME_U + " , " + DatabaseHelper.COLUMN_SURNAME_U + " ) " +
                                    " values " + " ( " + "'" + phone + "'" + " , " + "'" + pass + "'" + " , " +
                                    "'" + name + "'" + " , " + "'" +
                                    surname_reg.text.toString() + "'" + " ) "
                        )

                        Toast.makeText(
                            this, "Вы успешно зарегистрированы",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}