package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.TaskManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.rocketzly.lintplugin.LintHelper
import com.rocketzly.lintplugin.utils.LintUtils
import com.rocketzly.lintplugin.utils.ReflectionUtils
import org.gradle.api.Project
import java.util.stream.Collectors

/**
 * User: Rocket
 * Date: 2020/9/4
 * Time: 11:39 AM
 */
class LintTaskHelper : LintHelper {


    override fun apply(project: Project) {
        project.afterEvaluate {
            var variantManager = LintUtils.getVariantManager(project) ?: return@afterEvaluate
            val variantScopesForLint = variantManager.variantScopes.stream()
                .filter { variantScope: VariantScope ->
                    ReflectionUtils.invokeMethod(
                        TaskManager::class.java,
                        "isLintVariant",
                        arrayOf(VariantScope::class.java),
                        arrayOf(variantScope)
                    ) as Boolean
                }.collect(Collectors.toList())

            var scope = variantScopesForLint.find {
                LintUtils.getFullVariantName(it).contains("debug")
            }//没找到包含debug的variant的话，默认取第一个可能会有问题

            if (scope == null) scope = variantScopesForLint[0]

            TaskFactoryImpl(project.tasks).apply {
                register(
                    LintCreationAction.FullCreationAction(
                        project, scope!!, variantScopesForLint
                    )
                )//初始化全量lintTask
                register(
                    LintCreationAction.IncrementCreationAction(
                        project, scope, variantScopesForLint
                    )
                )//初始化增量lintTask
            }
        }
    }
}