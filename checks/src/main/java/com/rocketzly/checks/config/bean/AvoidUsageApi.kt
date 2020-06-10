package com.rocketzly.checks.config.bean


/**
 * 避免调用api
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:14 AM
 */
class AvoidUsageApi(
    var avoidUsageMethodList: MutableList<AvoidUsageMethod> = mutableListOf()
) {
    /**
     * 获取方法名list
     */
    fun getAvoidMethodNameList(): List<String> =
        avoidUsageMethodList.map {
            it.name
        }

    /**
     * 通过方法名获取AvoidUsageMethod
     */
    fun getAvoidUsageMethodByName(name: String): AvoidUsageMethod {
        avoidUsageMethodList.forEach {
            if (it.name == (name)) return it
        }
        return AvoidUsageMethod("", "", "", "")
    }
}

/**
 * 避免调用的方法
 */
data class AvoidUsageMethod(
    val name: String,
    val message: String,
    val severity: String = "error",
    val inClass: String
)