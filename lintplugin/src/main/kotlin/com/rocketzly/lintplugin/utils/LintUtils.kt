package com.rocketzly.lintplugin.utils

import com.rocketzly.lintplugin.task.LintCreationAction.Companion.TASK_NAME_LINT_FULL
import com.rocketzly.lintplugin.task.LintCreationAction.Companion.TASK_NAME_LINT_INCREMENT
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by rocketzly on 2020/10/11.
 */
class LintUtils {
    companion object {
        fun checkTaskIsIncrementOrFullLint(task: Task, project: Project? = null) =
            (task.name == TASK_NAME_LINT_INCREMENT || task.name == TASK_NAME_LINT_FULL)
                    && (project == null || task.project == project)
    }
}