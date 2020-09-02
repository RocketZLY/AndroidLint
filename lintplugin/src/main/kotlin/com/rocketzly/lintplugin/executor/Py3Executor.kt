package com.rocketzly.lintplugin.executor

/**
 * User: Rocket
 * Date: 2020/9/2
 * Time: 11:51 AM
 */
class Py3Executor : ScriptExecutor.Executor {
    override fun exec(
        scriptPath: String,
        reportPath: String,
        userName: String,
        branchName: String,
        moduleName: String,
        errorCount: Int,
        errorSummary: HashSet<String>
    ) {
        val command =
            "python3 $scriptPath --reportPath=$reportPath --userName=$userName --branchName=$branchName --moduleName=$moduleName --errorCount=$errorCount"
        println("执行命令如下：")
        println(command)
        val process = Runtime.getRuntime().exec(command)

        val stdout = String(process.inputStream.readBytes(), Charsets.UTF_8)
        val stderr = String(process.errorStream.readBytes(), Charsets.UTF_8)
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