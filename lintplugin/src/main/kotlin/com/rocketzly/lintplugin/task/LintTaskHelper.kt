package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.rocketzly.gradle.AgpApiFactory
import com.rocketzly.gradle.IAgpApi
import com.rocketzly.lintplugin.LintHelper
import com.rocketzly.lintplugin.dependency.DependencyHelper
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
            val agpApi = AgpApiFactory.getAgpApi()
            agpApi.injectPatch(project, DependencyHelper.version)
            //创建增量lint
            createLintTasks(agpApi, project, LintCreationAction.TASK_NAME_LINT_INCREMENT)
            //全量lint
            createLintTasks(agpApi, project, LintCreationAction.TASK_NAME_LINT_FULL)
        }
    }

    private fun createLintTasks(agpApi: IAgpApi, project: Project, taskName: String) {
        agpApi.createLintTasks(
            project,
            taskName,
            null,
            object : TaskConfigAction<Task> {
                override fun configure(task: Task) {
                    task.doFirst {
                        //支持增量扫描功能，在classloader加载成功后，将lintPatch移动到path最前面，确保补丁一定被应用
                        agpApi.replaceLintClassLoader(task)
                    }
                    task.doLast {
                        //lint类都是通过该classloader加载，而loader是静态变量，只会创建一次(debug的时候每次都是重新创建，直接运行的时候不会重新创建)，
                        //又由于increment和full需要插入类到该classloader头部实现部分功能
                        //功能1：increment和full在lint3.6.0以前需要patch修复lint的bug
                        //功能2：increment还额外需要增加增量扫描功能
                        //所以在increment和full执行前将classloader置为null，使其重新加载，将patch类加载进来
                        //在increment和full执行后将classloader置为null，避免插入类对之后执行的其他lintTask造成的影响
                        agpApi.resetLintClassLoader()
                    }
                }
            }
        )
    }
}