package com.rocketzly.lintplugin.executor

import com.rocketzly.lintplugin.log.printSplitLine
import com.rocketzly.lintplugin.task.LintOptionsInjector.Companion.HTML_OUTPUT_RELATIVE_PATH
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/9/2
 * Time: 11:46 AM
 * 脚本执行工厂，目前其实只执行py3
 */
class ScriptExecutor {

    companion object {

        private const val TAG = "lint发现错误执行脚本信息"
        private const val PARAM_NAME_FAIL_SCRIPT_PATH = "failScriptPath"

        fun exec(
            project: Project,
            errorCount: Int,
            errorSummary: HashSet<String>
        ) {
            printSplitLine(TAG)

            val scriptPath = project.properties[PARAM_NAME_FAIL_SCRIPT_PATH] as? String?
            if (scriptPath == null || scriptPath.isEmpty()) {
                println("未输入failScriptPath参数，将不会自动执行脚本")
                printSplitLine(TAG)
                return
            }

            val executor = getExecutor("")
            //git log -1 --pretty=format:'%aN' 用这个命令获取提交人有一定隐患，用rebase的时候可能会出问题
            val userName = String(
                Runtime.getRuntime()
                    .exec("git log -1 --pretty=format:%aN").inputStream.readBytes(),
                Charsets.UTF_8
            )
            val reportPath = project.file(HTML_OUTPUT_RELATIVE_PATH).absolutePath
            executor.exec(
                scriptPath,
                reportPath,
                userName,
                project.name,
                errorCount,
                errorSummary
            )

            printSplitLine(TAG)
        }

        private fun getExecutor(type: String): Executor = Py3Executor()
    }

    interface Executor {
        fun exec(
            scriptPath: String,
            reportPath: String,
            userName: String,
            moduleName: String,
            errorCount: Int,
            errorSummary: HashSet<String>
        )
    }
}