package com.rocketzly.androidlint

import android.graphics.Color

/**
 * Created by rocketzly on 2020/8/9.
 */
class HandleExceptionMethod {

    fun test() {
        Color.parseColor("1")
    }
}