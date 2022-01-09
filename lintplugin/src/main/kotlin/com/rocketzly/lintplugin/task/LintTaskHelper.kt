package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.rocketzly.gradle.AgpApiFactory
import com.rocketzly.lintplugin.LintHelper
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * User: Rocket
 * Date: 2020/9/4
 * Time: 11:39 AM
 */
class LintTaskHelper : LintHelper {

    override fun apply(project: Project) {
        project.afterEvaluate {
            //创建增量lint
            createLintTasks(project, LintCreationAction.TASK_NAME_LINT_INCREMENT)
            //全量lint
            createLintTasks(project, LintCreationAction.TASK_NAME_LINT_FULL)
        }
    }

    private fun createLintTasks(project: Project, taskName: String) {
        AgpApiFactory.getAgpApi()
            .createLintTasks(
                project,
                taskName,
                object : PreConfigAction {
                    override fun preConfigure(taskName: String) {
                        LintCreationAction.preConfigure(project, taskName)
                    }
                },
                object : TaskConfigAction<Task> {
                    override fun configure(task: Task) {
                        LintCreationAction.configure(project, task)
                    }
                }
            )
    }
}