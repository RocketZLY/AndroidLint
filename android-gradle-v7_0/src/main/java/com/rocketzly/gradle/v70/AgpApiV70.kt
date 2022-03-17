package com.rocketzly.gradle.v70

import com.android.build.api.component.impl.TestComponentImpl
import com.android.build.api.variant.impl.VariantBuilderImpl
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.internal.VariantManager
import com.android.build.gradle.internal.component.AndroidTestCreationConfig
import com.android.build.gradle.internal.component.UnitTestCreationConfig
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.build.gradle.internal.lint.AndroidLintCopyReportTask
import com.android.build.gradle.internal.lint.AndroidLintTask
import com.android.build.gradle.internal.lint.AndroidLintTask.SingleVariantCreationAction.Companion.registerLintReportArtifacts
import com.android.build.gradle.internal.lint.VariantWithTests
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.build.gradle.internal.tasks.factory.PreConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskConfigAction
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.android.build.gradle.internal.variant.ComponentInfo
import com.android.build.gradle.options.BooleanOption
import com.rocketzly.gradle.api.IAgpApi
import com.rocketzly.gradle.api.bean.LintOptionData
import com.rocketzly.gradle.api.utils.ReflectionUtils
import com.rocketzly.gradle.api.utils.androidPlugin
import com.rocketzly.gradle.api.utils.globalScope
import com.rocketzly.gradle.api.utils.variantManager
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * User: Rocket
 * Date: 2022/1/9
 * Time: 7:23 下午
 */
class AgpApiV70 : IAgpApi {

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
        val globalScope = project.androidPlugin.globalScope as GlobalScope
        val variantManager =
            project.androidPlugin.variantManager as VariantManager<out VariantBuilderImpl, out VariantImpl>
        val variantPropertiesList =
            variantManager.mainComponents.map(
                ComponentInfo<out VariantBuilderImpl, out VariantImpl>::variant
            )
        val testComponentPropertiesList = variantManager.testComponents

        val variantsWithTests = attachTestsToVariants(
            variantPropertiesList = variantPropertiesList,
            testComponentPropertiesList = if (globalScope.extension.lintOptions.isIgnoreTestSources) {
                listOf()
            } else {
                testComponentPropertiesList
            }
        )

        val taskFactory = TaskFactoryImpl(project.tasks)
        val needsCopyReportTask = needsCopyReportTask(globalScope.extension.lintOptions)

        for (variantWithTests in variantsWithTests.values) {

            val variantLintTask =
                taskFactory.register(
                    CustomSingleVariantCreationAction(
                        variantWithTests, taskName
                    ),
                    preConfigAction,
                    secondaryAction
                )
            if (needsCopyReportTask) {
                val copyLintReportTask =
                    taskFactory.register(AndroidLintCopyReportTask.CreationAction(variantWithTests.main))
                variantLintTask.configure {
                    it.finalizedBy(copyLintReportTask)
                }
            }
        }
    }

    private fun attachTestsToVariants(
        variantPropertiesList: List<VariantImpl>,
        testComponentPropertiesList: List<TestComponentImpl>
    ): LinkedHashMap<String, VariantWithTests> {
        val variantsWithTests = LinkedHashMap<String, VariantWithTests>()
        for (variant in variantPropertiesList) {
            variantsWithTests[variant.name] = VariantWithTests(variant)
        }
        for (testComponent in testComponentPropertiesList) {
            val key = testComponent.testedConfig.name
            val current = variantsWithTests[key]!!
            when (testComponent) {
                is AndroidTestCreationConfig -> {
                    check(current.androidTest == null) {
                        "Component ${current.main} appears to have two conflicting android test components ${current.androidTest} and $testComponent"
                    }
                    variantsWithTests[key] =
                        VariantWithTests(current.main, testComponent, current.unitTest)
                }
                is UnitTestCreationConfig -> {
                    check(current.unitTest == null) {
                        "Component ${current.main} appears to have two conflicting unit test components ${current.unitTest} and $testComponent"
                    }
                    variantsWithTests[key] =
                        VariantWithTests(current.main, current.androidTest, testComponent)
                }
                else -> throw IllegalStateException("Unexpected test component type")
            }
        }
        return variantsWithTests
    }

    private fun File.isLintStdout() = this.path == File("stdout").path
    private fun File.isLintStderr() = this.path == File("stderr").path
    private fun needsCopyReportTask(lintOptions: LintOptions): Boolean {
        val textOutput = lintOptions.textOutput
        return (lintOptions.textReport && textOutput != null && !textOutput.isLintStdout() && !textOutput.isLintStderr()) ||
                (lintOptions.htmlReport && lintOptions.htmlOutput != null) ||
                (lintOptions.xmlReport && lintOptions.xmlOutput != null) ||
                (lintOptions.sarifReport && lintOptions.sarifOutput != null)
    }

    class CustomSingleVariantCreationAction(variant: VariantWithTests, taskName: String) :
        AndroidLintTask.VariantCreationAction(variant) {
        override val name: String = creationConfig.computeTaskName(taskName)
        override val fatalOnly: Boolean get() = false
        override val autoFix: Boolean get() = false
        override val description: String get() = "Run lint on the ${creationConfig.name} variant"
        override val checkDependencies: Boolean
            get() =
                creationConfig.globalScope.extension.lintOptions.isCheckDependencies
                        && !variant.main.variantType.isDynamicFeature
        override val reportOnly: Boolean
            get() = creationConfig.services.projectOptions.get(BooleanOption.USE_LINT_PARTIAL_ANALYSIS)

        override fun handleProvider(taskProvider: TaskProvider<AndroidLintTask>) {
            registerLintReportArtifacts(
                taskProvider,
                creationConfig.artifacts,
                creationConfig.name,
                creationConfig.services.projectInfo.getReportsDir()
            )
        }

        override fun configureOutputSettings(task: AndroidLintTask) {
            ReflectionUtils.invokeMethod(
                task,
                "configureOutputSettings",
                arrayOf(LintOptions::class.java),
                arrayOf(creationConfig.globalScope.extension.lintOptions)
            )
        }
    }

    override fun addPatchDependence(project: Project, version: String) {
//        val config = project.configurations.create(LINT_PATCH)
//        config.isVisible = false
//        config.isTransitive = true
//        config.isCanBeConsumed = false
//        config.description = "The lint patch classpath"
//        project.dependencies.add(
//            config.name, "$PATCH_GROUP_ID:$PATCH_ARTIFACT_ID:$version"
//        )
    }

    override fun updateLintOption(task: Task, lintOptionData: LintOptionData) {
//        (task as LintPerVariantTask).lintOptions.apply {
//            checkOnly(*lintOptionData.check.toTypedArray())
//            xmlOutput = lintOptionData.xmlOutput
//            htmlOutput = lintOptionData.htmlOutput
//            isAbortOnError = lintOptionData.abortOnError
//            baselineFile = lintOptionData.baselineFile
//        }
    }

    override fun replaceLintClassLoader(task: Task) {
//        //模拟lintClassLoader创建过程，将补丁放到urls第一个，达到替换LintGradleClient的作用
//        val urls = mutableListOf<URL>()
//        //获取lint所需class依赖
//        val lintClassPath = (task as LintPerVariantTask).lintClassPath.files
//        //生成lintUrls
//        val companion =
//            ReflectionUtils.getFieldValue(ReflectiveLintRunner::class.java, "Companion")!!
//        val lintUrls = ReflectionUtils.invokeMethod(
//            companion,
//            "computeUrlsFromClassLoaderDelta",
//            arrayOf(Set::class.java),
//            arrayOf(lintClassPath)
//        ) as List<URL>
//
//        //拿到补丁依赖
//        val patchFiles = task.project.configurations.getByName(LINT_PATCH)
//        //剔除无用依赖，只保留patch
//        val patchUrls =
//            patchFiles
//                .filter { it.name.contains(PATCH_ARTIFACT_ID) }
//                .map { it.toURL() }
//
//        //合并url，把补丁插入urls第一个位置
//        urls.addAll(patchUrls)
//        urls.addAll(lintUrls)
//
//        //生成新的LintClassLoader
//        val l = DelegatingClassLoader(urls.toTypedArray())
//        //替换掉原始classLoader
//        ReflectionUtils.setFieldValue(ReflectiveLintRunner::class.java, "loader", l)
    }

    override fun resetLintClassLoader() {
//        ReflectionUtils.setFieldValue(ReflectiveLintRunner::class.java, "loader", null)
    }
}