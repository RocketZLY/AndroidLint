package com.rocketzly.gradle

import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * User: Rocket
 * Date: 2022/1/9
 * Time: 7:07 下午
 */
abstract class IAgpApi {

    /**
     * 根据variant创建LintTasks
     */
    abstract fun createLintTasks(
        project: Project,
        taskName: String,
        preConfigAction: PreConfigAction?,
        secondaryAction: TaskConfigAction<Task>?,
    )
}