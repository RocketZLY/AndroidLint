package com.rocketzly.gradle

import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.rocketzly.gradle.bean.LintOptionData
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

    val LINT_PATCH: String
        get() = "lintPatch"

    /**
     * 添加补丁依赖以支持增量扫描功能
     */
    fun addPatchDependence(project: Project, version: String)

    /**
     * 更新lintOption属性
     */
    fun updateLintOption(task: Task, lintOptionData: LintOptionData)

    /**
     * 替换lintClassLoader
     * 模拟lintClassLoader创建过程，将补丁插入到urls第一个，达到替换LintGradleClient的作用
     */
    fun replaceLintClassLoader(task: Task)

    /**
     * 重置加载lint的classLoader
     * 在increment执行后将classloader置为null，避免补丁对之后执行的其他lintTask造成的影响
     */
    fun resetLintClassLoader()
}