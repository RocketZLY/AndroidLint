package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.scope.VariantScope
import com.android.tools.lint.gradle.api.DelegatingClassLoader
import com.android.tools.lint.gradle.api.ReflectiveLintRunner
import com.rocketzly.lintplugin.dependency.DependencyHelper
import com.rocketzly.lintplugin.utils.ReflectionUtils
import org.gradle.api.Project
import sun.misc.URLClassPath
import java.net.URL
import kotlin.concurrent.thread

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
            //加入补丁修复lint的bug同时支持增量扫描功能，需要在super#configure之前调用
            DependencyHelper.injectLintPatch(project)
            super.configure(task)
            //修改lintOptions，需要在super#configure之后调用
            LintOptionsInjector.inject(project, task.lintOptions)

            task.doFirst {
                resetLintClassLoader()
                ensurePatchSuccess()
            }
            task.doLast {
                resetLintClassLoader()
            }
        }


        private fun resetLintClassLoader() {
            //lint类都是通过该classloader加载，而loader是静态变量，只会创建一次(debug的时候每次都是重新创建，直接运行的时候不会重新创建)，
            //又由于increment和full需要插入类到该classloader头部实现部分功能
            //功能1：increment和full在lint3.6.0以前需要patch修复lint的bug
            //功能2：increment还额外需要增加增量扫描功能
            //所以在increment和full执行前将classloader置为null，使其重新加载，将patch类加载进来
            //在increment和full执行后将classloader置为null，避免插入类对之后执行的其他lintTask造成的影响
            ReflectionUtils.setFieldValue(ReflectiveLintRunner::class.java, "loader", null)
        }

        /**
         * 确保补丁成功加载
         * 原理：在classloader加载成功后，将lintPatch移动到path最前面，确保补丁一定被应用
         */
        private fun ensurePatchSuccess() {
            thread {
                while (true) {
                    var loader = ReflectionUtils.getFieldValue(
                        ReflectiveLintRunner::class.java,
                        "loader"
                    ) as DelegatingClassLoader?
                    if (loader != null) {
                        val urlClassPath =
                            ReflectionUtils.getFieldValue(loader, "ucp") as URLClassPath
                        val path =
                            ReflectionUtils.getFieldValue(urlClassPath, "path") as ArrayList<URL>
                        var index = -1
                        path.forEachIndexed { i, url ->
                            if (url.path.contains("lintPatch")) {
                                index = i
                                return@forEachIndexed
                            }
                        }
                        if (index != -1)
                            path.add(0, path.removeAt(index))
                        return@thread
                    }
                }
            }
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
    ) : Action(project, TASK_NAME_LINT_INCREMENT, scope, variantScopes)

}





