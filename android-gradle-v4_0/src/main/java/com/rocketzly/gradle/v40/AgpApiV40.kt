package com.rocketzly.gradle.v40

import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.android.build.gradle.tasks.LintPerVariantTask
import com.rocketzly.gradle.IAgpApi
import com.rocketzly.gradle.utils.androidPlugin
import com.rocketzly.gradle.utils.variantManager
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * User: Rocket
 * Date: 2022/1/9
 * Time: 7:22 下午
 */
class AgpApiV40 : IAgpApi {

    override fun createLintTasks(
        project: Project,
        taskName: String,
        preConfigAction: PreConfigAction?,
        secondaryAction: TaskConfigAction<Task>?
    ) {
        val variantScopes = (project.androidPlugin.variantManager as VariantManager).variantScopes
        val variantScopesForLint =
            variantScopes
                .filter { !it.type.isTestComponent }
                .filter { isLintVariant(it) }
        for (variantScope in variantScopes) {
            val action = object :
                LintPerVariantTask.CreationAction(variantScope, variantScopesForLint) {
                override val name: String
                    get() = variantScope.getTaskName(taskName)
            }
            TaskFactoryImpl(project.tasks).register(action, preConfigAction, secondaryAction)
        }
    }

    private fun isLintVariant(variantScope: VariantScope): Boolean {
        // Only create lint targets for variants like debug and release, not debugTest
        val variantType = variantScope.variantDslInfo.variantType
        return !variantType.isForTesting
    }

    override fun injectPatch(project: Project, version: String) {
    }

    override fun replaceLintClassLoader(task: Task) {
    }

    override fun resetLintClassLoader() {
    }
}