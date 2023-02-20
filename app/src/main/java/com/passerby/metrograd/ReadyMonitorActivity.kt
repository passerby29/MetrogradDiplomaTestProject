package com.passerby.metrograd

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ready_monitor.*

class ReadyMonitorActivity : AppCompatActivity() {

    var sqlHelper: DatabaseHelper? = null
    var db: SQLiteDatabase? = null
    lateinit var mNames: Array<String?>
    lateinit var mURLs: Array<String?>
    var adapter: SimpleCursorAdapter? = null
    var userStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ready_monitor)
        supportActionBar?.hide()

        sqlHelper = DatabaseHelper(this)
        sqlHelper!!.createDB()

        val intent = intent
        userStatus = intent.getStringExtra("userStatus").toString()

        if (userStatus == "1") {
            layout_false.visibility = View.GONE
            layout_true.visibility = View.VISIBLE
        } else {
            layout_true.visibility = View.GONE
            layout_false.visibility = View.VISIBLE
        }

    }

    fun newMonitor(view: View) {
        val intent = Intent(this, MonitorActivity::class.java)
        intent.putExtra("userStatus", userStatus)
        startActivity(intent)
    }

    fun contacts(view: View) {
        val intent = Intent(this, ContactsActivity::class.java)
        intent.putExtra("userStatus", userStatus)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        db = sqlHelper!!.open()
        var mCursor = db!!.rawQuery(" select * from " + DatabaseHelper.TABLE_M, null)

        mNames = arrayOf(DatabaseHelper.COLUMN_NAME_M)

        adapter = SimpleCursorAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            mCursor,
            mNames,
            intArrayOf(android.R.id.text1),
            0
        )
        ready_monitors_list.adapter = adapter

        mURLs = arrayOfNulls(mCursor.count)
        var columnIndexURL = mCursor.getColumnIndex(DatabaseHelper.COLUMN_LINK_M)

        if (mCursor.moveToFirst()) {
            for (i in 0 until mCursor.count) {
                mURLs[i] = mCursor.getString(columnIndexURL)
                mCursor.moveToNext()
            }
        }

        ready_monitors_list.setOnItemClickListener { parent, view, position, id ->
            val url = mURLs[position].toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
