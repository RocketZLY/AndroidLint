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
interface IAgpApi {

    /**
     * 根据variant创建LintTasks
     */
    fun createLintTasks(
        project: Project,
        taskName: String,
        preConfigAction: PreConfigAction?,
        secondaryAction: TaskConfigAction<Task>?,
    )

    val  LINT_PATCH: String
        get() = "lintPatch"

    /**
     * 加入补丁以支持增量扫描功能
     */
    fun injectPatch(project: Project, version: String)

    /**
     * 替换lintClassLoader
     * 模拟lintClassLoader创建过程，将补丁插入到urls第一个，达到替换LintGradleClient的作用
     */
    fun replaceLintClassLoader(task: Task)

    /**
     * 重置加载lint的classLoader
     * 在increment和full执行后将classloader置为null，避免插入类对之后执行的其他lintTask造成的影响
     */
    fun resetLintClassLoader()
}