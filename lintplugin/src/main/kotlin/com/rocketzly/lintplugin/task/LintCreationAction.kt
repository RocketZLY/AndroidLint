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
        const val PARAM_NAME_TARGET = "target"
        const val PARAM_NAME_CURRENT = "current"
    }

    open class Action(
        private val project: Project,
        private val taskName: String,
        private val scope: VariantScope,
        private val variantScopes: List<VariantScope>
    ) : LintTask.CreationAction(taskName, scope, variantScopes) {
        override fun configure(task: LintTask) {
            super.configure(task)
            //lint类都是通过该classloader加载，而loader是静态变量，只会创建一次，
            //又由于increment需要插入类到该classloader头部，而full又不需要
            //所以在每次执行时强制改为null废除缓存，每次重新加载避免问题
            ReflectiveLintRunner.loader = null
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
            DependencyHelper.injectLintIncrement(project)//加入支持增量LintGradleClient类替换原有类
            super.configure(task)
        }
    }

}





