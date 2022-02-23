package com.rocketzly.gradle.v41

import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.android.build.gradle.tasks.LintPerVariantTask
import com.android.tools.lint.gradle.api.DelegatingClassLoader
import com.android.tools.lint.gradle.api.ReflectiveLintRunner
import com.rocketzly.gradle.api.IAgpApi
import com.rocketzly.gradle.api.bean.LintOptionData
import com.rocketzly.gradle.api.utils.ReflectionUtils
import com.rocketzly.gradle.api.utils.androidPlugin
import com.rocketzly.gradle.api.utils.variantManager
import org.gradle.api.Project
import org.gradle.api.Task
import java.net.URL

/**
 * User: Rocket
 * Date: 2022/1/9
 * Time: 7:23 下午
 */
class AgpApiV41 : IAgpApi {

    companion object {
        private const val PATCH_GROUP_ID = "com.github.rocketzly"
        private const val PATCH_ARTIFACT_ID = "android-gradle-v4_1"
    }

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

    override fun addPatchDependence(project: Project, version: String) {
        val config = project.configurations.create(LINT_PATCH)
        config.isVisible = false
        config.isTransitive = true
        config.isCanBeConsumed = false
        config.description = "The lint patch classpath"
        project.dependencies.add(
            config.name, "$PATCH_GROUP_ID:$PATCH_ARTIFACT_ID:$version"
        )
    }

    override fun updateLintOption(task: Task, lintOptionData: LintOptionData) {
        (task as LintPerVariantTask).lintOptions.apply {
            checkOnly(*lintOptionData.check.toTypedArray())
            xmlOutput = lintOptionData.xmlOutput
            htmlOutput = lintOptionData.htmlOutput
            isAbortOnError = lintOptionData.abortOnError
            baselineFile = lintOptionData.baselineFile
        }
    }

    override fun replaceLintClassLoader(task: Task) {
        //模拟lintClassLoader创建过程，将补丁放到urls第一个，达到替换LintGradleClient的作用
        val urls = mutableListOf<URL>()
        //获取lint所需class依赖
        val lintClassPath = (task as LintPerVariantTask).lintClassPath.files
        //生成lintUrls
        val companion =
            ReflectionUtils.getFieldValue(ReflectiveLintRunner::class.java, "Companion")!!
        val lintUrls = ReflectionUtils.invokeMethod(
            companion,
            "computeUrlsFromClassLoaderDelta",
            arrayOf(Set::class.java),
            arrayOf(lintClassPath)
        ) as List<URL>

        //拿到补丁依赖
        val patchFiles = task.project.configurations.getByName(LINT_PATCH)
        //剔除无用依赖，只保留patch
        val patchUrls =
            patchFiles
                .filter { it.name.contains(PATCH_ARTIFACT_ID) }
                .map { it.toURL() }

        //合并url，把补丁插入urls第一个位置
        urls.addAll(patchUrls)
        urls.addAll(lintUrls)

        //生成新的LintClassLoader
        val l = DelegatingClassLoader(urls.toTypedArray())
        //替换掉原始classLoader
        ReflectionUtils.setFieldValue(ReflectiveLintRunner::class.java, "loader", l)
    }

    override fun resetLintClassLoader() {
        ReflectionUtils.setFieldValue(ReflectiveLintRunner::class.java, "loader", null)
    }
}