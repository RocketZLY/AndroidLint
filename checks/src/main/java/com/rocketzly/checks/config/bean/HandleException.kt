package com.rocketzly.checks.config.bean

/**
 * 调用指定API时，需要加try-catch处理指定类型的异常
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:06 AM
 */
data class HandleException(
    val method: MutableList<HandleExceptionMethod> = mutableListOf()
) {
    /**
     * 获取需要处理异常的方法名
     */
    fun getHandleExceptionMethodNameList() =
        method.map { it.name }

    /**
     * 通过名字获取需要处理异常的方法
     */
    fun getHandleExceptionMethodByName(name: String): HandleExceptionMethod {
        method.forEach {
            if (it.name == name) return it
        }
        return HandleExceptionMethod("", "", "", "", "")
    }
}

data class HandleExceptionMethod(
    val name: String,
    val exception: String,
    val message: String,
    val severity: String? = "error",
    val inClass: String
)
