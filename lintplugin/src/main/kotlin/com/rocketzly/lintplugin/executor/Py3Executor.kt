package com.rocketzly.lintplugin.executor

import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/9/8
 * Time: 6:57 PM
 */
class Py3Executor : BaseExecutor(), ScriptExecutor.Executor {
    override fun exec(
        project: Project,
        scriptPath: String,
        reportPath: String,
        userName: String,
        moduleName: String,
        errorCount: Int,
        errorSummary: HashSet<String>
    ) {
        val command =
            "python3 $scriptPath --reportPath=$reportPath --userName=$userName --moduleName=$moduleName --errorCount=$errorCount"
        exec(command)
    }


}