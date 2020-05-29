package com.rocketzly.androidlint

import java.io.Serializable

/**
 * User: Rocket
 * Date: 2020/5/27
 * Time: 7:12 PM
 */
class SerializableBean : Serializable {
    var serializableField: InnerSerializableBean? = null
}

class InnerSerializableBean : Serializable {
    var commonBean: CommonBean? = null
}

class CommonBean{
    private var s: String = "abc"
}