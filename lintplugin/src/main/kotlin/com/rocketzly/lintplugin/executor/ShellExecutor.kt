package com.rocketzly.lintplugin.executor

import org.gradle.api.Project
import java.io.File
import java.lang.StringBuilder

/**
 * Created by rocketzly on 2020/9/20.
 */
class ShellExecutor : BaseExecutor(), ScriptExecutor.Executor {
    override fun exec(
        project: Project,
        scriptPath: String,
        reportPath: String,
        userName: String,
        moduleName: String,
        errorCount: Int,
        errorSummary: HashSet<String>
    ) {
        //由于shell传长参数不方便，所以通过写文件方式来传递参数
        val file = File(project.buildDir, "tmp/lint/scriptParams.txt")
        if (file.exists()) file.delete()
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()

        val params = StringBuilder()
            .append("reportPath=$reportPath\n")
            .append("userName=$userName\n")
            .append("moduleName=$moduleName\n")
            .append("errorCount=$errorCount")
            .toString()

        file.writeText(params)
        val command =
            "sh $scriptPath ${file.absolutePath}"
        exec(command)
    }
}