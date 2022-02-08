package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.rocketzly.gradle.AgpApiFactory
import com.rocketzly.gradle.IAgpApi
import com.rocketzly.lintplugin.LintHelper
import com.rocketzly.lintplugin.dependency.DependencyHelper
import org.gradle.api.Project
import org.gradle.api.Task
import java.util.*

/**
 * User: Rocket
 * Date: 2020/9/4
 * Time: 11:39 AM
 */
class LintTaskHelper : LintHelper {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val agpApi = AgpApiFactory.getAgpApi()
            //加入补丁以支持增量扫描功能
            agpApi.injectPatch(project, DependencyHelper.version)
            //创建增量lint
            createLintTasks(agpApi, project, LintCreationAction.TASK_NAME_LINT_INCREMENT)
            //全量lint
            createLintTasks(agpApi, project, LintCreationAction.TASK_NAME_LINT_FULL)
        }
    }

    private fun createLintTasks(agpApi: IAgpApi, project: Project, taskName: String) {
        val secondaryAction = if (taskName == LintCreationAction.TASK_NAME_LINT_INCREMENT) {
            object : TaskConfigAction<Task> {
                override fun configure(task: Task) {
                    task.doFirst {
                        //替换lintClassLoader
                        //模拟lintClassLoader创建过程，将补丁插入到urls第一个，达到替换LintGradleClient的作用
                        agpApi.replaceLintClassLoader(task)
                        //之所以用的延迟去重置classLoader而不是doLast，原因是如果检查发现错误停止任务doLast不会走
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                //重置加载lint的classLoader
                                //在increment执行后将classloader置为null，避免补丁对之后执行的其他lintTask造成的影响
                                agpApi.resetLintClassLoader()
                            }
                        }, 1500)
                    }
                }
            }
        } else {
            null
        }
        agpApi.createLintTasks(
            project,
            taskName,
            null,
            secondaryAction
        )
    }
}