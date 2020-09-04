package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.tasks.LintBaseTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * User: Rocket
 * Date: 2020/9/3
 * Time: 3:52 PM
 * lintTask，模拟LintPerVariantTask进行lint扫描，尽量还原的源码，没做太大改动，方便后期适配agp版本变更
 */
open class LintTask : LintBaseTask() {

    private var allInputs: ConfigurableFileCollection? = null
    private var variantName: String? = null
    private var variantInputs: VariantInputs? = null

    @InputFiles
    @Optional
    open fun getAllInputs(): FileCollection? {
        return allInputs
    }

    @TaskAction
    fun lint() {
        val descriptor = object : LintBaseTaskDescriptor() {

            /**
             * com.android.tools.lint.gradle.LintGradleExecution#analyze会判断
             */
            override val variantName: String? = this@LintTask.variantName

            /**
             * com.android.tools.lint.gradle.LintGradleExecution#lintSingleVariant用来作为lint扫描参数
             */
            override fun getVariantInputs(variantName: String): VariantInputs? = variantInputs

        }

        runLint(descriptor)
    }

    open class CreationAction(
        private val taskName: String,
        private val scope: VariantScope,
        private val variantScopes: List<VariantScope>
    ) : BaseCreationAction<LintTask>(scope.globalScope) {
        override val name: String = taskName

        override val type: Class<LintTask> = LintTask::class.java

        override fun configure(task: LintTask) {
            super.configure(task)
            task.apply {
                variantName = scope.fullVariantName//lint检测时会判断有没有该值，必须有
                variantInputs = VariantInputs(scope)//lint检测时会取该值，必须有
                allInputs = scope.globalScope.project.files()
                    .from(this.variantInputs!!.allInputs)//gradle增量任务

                for (variantScope in variantScopes) {//不知道干嘛的，反正是模拟LintPerVariantTask就直接照抄了
                    addJarArtifactsToInputs(allInputs, variantScope)
                }
                description = "run lint scanner"
            }
        }

    }
}