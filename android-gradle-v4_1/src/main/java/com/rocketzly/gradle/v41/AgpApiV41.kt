package com.rocketzly.gradle.v41

import com.android.build.gradle.internal.VariantManager
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
 * Time: 7:23 下午
 */
object AgpApiV41 : IAgpApi() {

    override fun createLintTasks(
        project: Project,
        taskName: String,
        preConfigAction: PreConfigAction?,
        secondaryAction: TaskConfigAction<Task>?
    ) {
        val allVariantProperties =
            (project.androidPlugin.variantManager as VariantManager<*, *>).mainComponents
                .map { it.properties }
        for (variantProperties in allVariantProperties) {
            val action = object :
                LintPerVariantTask.CreationAction(variantProperties, allVariantProperties) {
                override val name: String
                    get() = variantProperties.computeTaskName(taskName)
            }
            TaskFactoryImpl(project.tasks).register(action, preConfigAction, secondaryAction)
        }
    }
}