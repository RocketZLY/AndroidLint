package com.rocketzly.androidlint

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("zhuliyuan", "123")
        val sharedPreferences = getSharedPreferences("123", Context.MODE_PRIVATE)
        Thread()
        Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
        "".toInt()
    }
}
