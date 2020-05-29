package com.rocketzly.checks

import java.io.Serializable

/**
 * User: Rocket
 * Date: 2020/5/27
 * Time: 7:12 PM
 */
class SerializableBean : Serializable {
    private var serializableField: InnerSerializableBean? = null
}

class InnerSerializableBean : Serializable {
    private var commonBean: CommonBean? = null
}

class CommonBean{
    private var s: String = "abc"
}