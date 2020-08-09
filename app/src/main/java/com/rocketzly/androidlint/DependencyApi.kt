package com.rocketzly.androidlint

import java.lang.StringBuilder

/**
 * Created by rocketzly on 2020/8/9.
 */
class DependencyApi {

    fun test() {
        //实时没提示出来，report是有的，以report为准
        StringBuilder().append("123")
    }
}