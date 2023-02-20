package com.passerby.metrograd

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_monitor.*
import kotlinx.android.synthetic.main.monitoring_list.*
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter

class MonitorActivity : AppCompatActivity() {

    var sqlHelper: DatabaseHelper? = null
    var db: SQLiteDatabase? = null
    lateinit var gNames: Array<String?>
    lateinit var gPrices: Array<String?>
    var userStatus: String? = null
    private var myAdapter: MyAdapter? = null
    var myItems: ArrayList<ListItem> = ArrayList<ListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)
        supportActionBar?.hide()
        monitors_list.itemsCanFocus = true

        sqlHelper = DatabaseHelper(this)
        sqlHelper!!.createDB()

        val intent = intent
        userStatus = intent.getStringExtra("userStatus").toString()
        db = sqlHelper!!.open()

        myAdapter = MyAdapter()
        monitors_list.adapter = myAdapter
    }

    inner class MyAdapter : BaseAdapter() {
        private var layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return myItems.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            gPrices = arrayOfNulls(32)
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                convertView = layoutInflater.inflate(R.layout.monitoring_list, null)
                holder.caption = convertView.findViewById<EditText>(R.id.item)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder.caption.setText(myItems[position].caption)
            holder.caption.id = position


            //we need to update adapter once we finish with editing
            holder.caption.onFocusChangeListener =
                OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        val position = v.id
                        val Caption = v as EditText
                        myItems[position].caption = Caption.text.toString().trim()
                    }
                }
            return convertView
        }

        init {
            val gCursor = db!!.rawQuery(" select * from " + DatabaseHelper.TABLE_G, null)
            val columnIndexName = gCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_G)
            gNames = arrayOfNulls(gCursor.count)
            if (gCursor.moveToFirst()) {
                for (i in 0 until 32) {
                    gNames[i] = gCursor.getString(columnIndexName)
                    gCursor.moveToNext()
                    val listItem = ListItem()
                    listItem.caption = gNames[i].toString()
                    myItems.add(listItem)
                }
            }
            notifyDataSetChanged()
        }
    }

    class ViewHolder {
        lateinit var caption: EditText
    }

    class ListItem {
        var caption: String? = null
    }

    fun newMonitorIntent(view: View) {
        val intent = Intent(this, ReadyMonitorActivity::class.java)
        intent.putExtra("userStatus", userStatus)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNewMonitor(view: View?) {
        val street = streetEditText.text.toString().trim()
        val house = houseEditText.text.toString().trim()
        val date = now().toString()
        for (i in 0 until 32){
            gPrices[i] = myItems[i].caption.toString()
        }
        val formatted = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        if (street.isEmpty() || house.isEmpty()) {
            Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show()
        } else {
            val cursor = db!!.rawQuery(
                " select distinct tbl_name from sqlite_master " +
                        "where tbl_name = 'monitor.$street.$house.$formatted'",
                null
            )
            cursor.close()
            if (cursor != null) {
                if (cursor.count > 0) {
                    Toast.makeText(
                        this, "Мониторинг по данному магазину за сегодня уже существует",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    db!!.execSQL(
                        " create table 'monitor.$street.$house.$formatted" +
                                "' (name TEXT UNIQUE, price TEXT) "
                    )
                    Toast.makeText(
                        this, "Таблица создан, мониторинг сохарнен",
                        Toast.LENGTH_SHORT
                    ).show()

                    for (i in 0 until 32) {
                        db!!.execSQL(
                            " insert into 'monitor.$street.$house.$formatted' (" +
                                    "name, price) values ( " + "'" + gNames[i] + "'" + " , "
                                    + "'" + gPrices[i] + "'" + " ) "
                        )
                    }
                }
            }
        }
    }
}

class Goods(val gName: String)