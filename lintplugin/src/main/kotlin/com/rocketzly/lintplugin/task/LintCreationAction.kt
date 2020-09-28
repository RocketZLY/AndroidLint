package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.scope.VariantScope
import com.android.tools.lint.gradle.api.ReflectiveLintRunner
import com.rocketzly.lintplugin.dependency.DependencyHelper
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/9/4
 * Time: 2:32 PM
 * 创建LintTaskAction
 */
class LintCreationAction {

    companion object {
        const val TASK_NAME_LINT_FULL = "lintFull"
        const val TASK_NAME_LINT_INCREMENT = "lintIncrement"
        const val PARAM_NAME_BASELINE = "baseline"
        const val PARAM_NAME_REVISION = "revision"
    }

    open class Action(
        private val project: Project,
        private val taskName: String,
        private val scope: VariantScope,
        private val variantScopes: List<VariantScope>
    ) : LintTask.CreationAction(taskName, scope, variantScopes) {
        override fun configure(task: LintTask) {
            super.configure(task)
            LintOptionsInjector.inject(project, task.lintOptions)//修改lintOptions
        }
    }

    /**
     * 全量lintAction
     */
    class FullCreationAction(
        project: Project,
        scope: VariantScope,
        variantScopes: List<VariantScope>
    ) : Action(project, TASK_NAME_LINT_FULL, scope, variantScopes)

    /**
     * 增量lintAction
     */
    class IncrementCreationAction(
        private val project: Project,
        private val scope: VariantScope,
        private val variantScopes: List<VariantScope>
    ) : Action(project, TASK_NAME_LINT_INCREMENT, scope, variantScopes) {
        override fun configure(task: LintTask) {
            project.gradle.taskGraph.apply {
                beforeTask {
                    if (it.name == TASK_NAME_LINT_INCREMENT) {
                        resetLintClassLoader()
                    }

                }
                afterTask {
                    if (it.name == TASK_NAME_LINT_INCREMENT) {
                        resetLintClassLoader()
                    }
                }
            }
            DependencyHelper.injectLintIncrement(project)//加入支持增量LintGradleClient类替换原有类
            super.configure(task)
        }

        private fun resetLintClassLoader() {
            //lint类都是通过该classloader加载，而loader是静态变量，只会创建一次，
            //又由于increment需要插入类到该classloader头部实现增量扫描，而full和系统lint又不需要
            //所以在increment执行前将classloader置为null，使其重新加载将类插入
            //在increment执行后将classloader置为null，避免插入类对其他lintTask造成的影响
            ReflectiveLintRunner.loader = null
        }
    }

}





