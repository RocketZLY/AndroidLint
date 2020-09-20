package com.rocketzly.lintplugin.executor

/**
 * Created by rocketzly on 2020/9/20.
 */
open class BaseExecutor {

    fun exec(command: String) {
        println("执行命令如下：")
        println(command)
        val process = Runtime.getRuntime().exec(command)

        val stdout = String(process.inputStream.readBytes(), Charsets.UTF_8).removeSuffix("\n")
        val stderr = String(process.errorStream.readBytes(), Charsets.UTF_8).removeSuffix("\n")
        println()
        println("执行日志如下：")
        if (stdout.isNotEmpty()) {
            println("stdout:")
            println(stdout)
        }
        if (stderr.isNotEmpty()) {
            println("stderr:")
            println(stderr)
        }
    }
}