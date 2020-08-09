package com.rocketzly.androidlint

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by rocketzly on 2020/8/9.
 */
class AvoidUsageApi {

    fun method(context: Context) {
        context.getSharedPreferences("", Context.MODE_PRIVATE)

        Toast.makeText(context, "", Toast.LENGTH_SHORT).show()

        "".toInt()

        Log.i("zhuliyuan", "")
    }

    fun construction() {
        Thread()
    }

    class MyActivity : AppCompatActivity()

}