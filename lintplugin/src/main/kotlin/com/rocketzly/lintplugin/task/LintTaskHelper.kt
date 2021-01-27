package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.rocketzly.lintplugin.LintHelper
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
            var variantManager: VariantManager? = null
            val variantManagerStr = "variantManager"
            if (project.plugins.hasPlugin(AppPlugin::class.java)) {
                variantManager = ReflectionUtils.getFieldValue(project.plugins.getPlugin(AppPlugin::class.java), variantManagerStr) as VariantManager?
            } else if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
                variantManager = ReflectionUtils.getFieldValue(project.plugins.getPlugin(LibraryPlugin::class.java), variantManagerStr) as VariantManager?
            }
            if (variantManager == null) return@afterEvaluate

            val variantScopes = variantManager.variantScopes.stream()
                .filter { variantScope: VariantScope ->
                    isLintVariant(
                        variantScope
                    )
                }.collect(Collectors.toList())

            var scope = variantScopes.find {
                it.fullVariantName.contains("debug")
            }//没找到包含debug的variant的话，默认取第一个可能会有问题

            if (scope == null) scope = variantScopes[0]

            TaskFactoryImpl(project.tasks).apply {
                register(
                    LintCreationAction.FullCreationAction(
                        project, scope!!, variantScopes
                    )
                )//初始化全量lintTask
                register(
                    LintCreationAction.IncrementCreationAction(
                        project, scope, variantScopes
                    )
                )//初始化增量lintTask
            }
        }
    }

    private fun isLintVariant(variantScope: VariantScope): Boolean {
        // Only create lint targets for variants like debug and release, not debugTest
        val variantType =
            variantScope.variantConfiguration.type
        return !variantType.isForTesting && !variantType.isHybrid
    }

}