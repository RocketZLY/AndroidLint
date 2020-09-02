package com.rocketzly.lintplugin.executor

import com.rocketzly.lintplugin.extension.LintGlobalConfigExtension
import com.rocketzly.lintplugin.lintconfig.LintConfigHelper.Companion.HTML_OUTPUT_RELATIVE_PATH
import com.rocketzly.lintplugin.log.printSplitLine
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

        fun exec(
            project: Project,
            errorCount: Int,
            errorSummary: HashSet<String>
        ) {
            printSplitLine(TAG)

            val scriptPath =
                project.rootProject.extensions.getByType(LintGlobalConfigExtension::class.java).failScriptPath
            if (scriptPath.isEmpty()) {
                println("rootProject.lintGlobalConfig.failScriptPath未设置，将不会自动执行脚本")
                printSplitLine(TAG)
                return
            }

            val executor = getExecutor("")
            val userName = String(
                Runtime.getRuntime().exec("git config user.name").inputStream.readBytes(),
                Charsets.UTF_8
            )
            val branchName = String(
                Runtime.getRuntime()
                    .exec("git symbolic-ref --short -q HEAD").inputStream.readBytes(),
                Charsets.UTF_8
            )
            val reportPath = project.file(HTML_OUTPUT_RELATIVE_PATH).absolutePath
            executor.exec(
                scriptPath,
                reportPath,
                userName,
                branchName,
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
            branchName: String,
            moduleName: String,
            errorCount: Int,
            errorSummary: HashSet<String>
        )
    }
}