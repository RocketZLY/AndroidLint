package com.rocketzly.checks.config

/**
 * 避免调用api
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:14 AM
 */
class AvoidUsageApi(
    val avoidUsageMethodList: MutableList<AvoidUsageMethod> = mutableListOf()
) {
    /**
     * 获取方法名list
     */
    fun getAvoidMethodNameList(): List<String> =
        avoidUsageMethodList.map {
            if (!it.name.contains(".")) {//不是全路径名直接使用
                return@map it.name
            }
            it.name.substring(it.name.lastIndexOf(".") + 1)
        }

    /**
     * 通过方法名获取AvoidUsageMethod
     */
    fun getAvoidUsageMethodByName(name: String): AvoidUsageMethod =
        avoidUsageMethodList.filter {
            it.name.contains(name)
        }[0]

}

data class AvoidUsageMethod(
    val name: String = "",
    val message: String = "",
    val severity: String = "error"
) {
    /**
     * 获取类名
     */
    fun getClassName(): String {
        if (name.contains(".")) {
            return name.substring(0, name.lastIndexOf("."))
        }
        return ""
    }
}