package com.rocketzly.checks.detector

import com.android.tools.lint.checks.DataFlowAnalyzer
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiVariable
import com.rocketzly.checks.CUSTOM_CATEGORY
import com.rocketzly.checks.config.LintConfigProvider
import com.rocketzly.checks.config.LintParserKey
import com.rocketzly.checks.config.bean.DependencyApi
import com.rocketzly.checks.getQualifiedName
import com.rocketzly.checks.matcher.LintMatcher
import com.rocketzly.checks.report
import org.jetbrains.uast.*

/**
 * 有依赖关系api
 * 目前检查开始条件是[DependencyApi.triggerMethod]方法被调用，
 * 如果满足开始条件则检查[DependencyApi.triggerMethod]后面的方法，
 * 有没有调用[DependencyApi.dependencyMethod]方法如果没调用则report。
 *
 * User: Rocket
 * Date: 2020/6/16
 * Time: 10:09 AM
 */
class DependencyApiDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "使用${LintConfigProvider.CONFIG_FILE_NAME}中${LintParserKey.KEY_DEPENDENCY_API}配置的api时必须调用dependencyMethod方法"
        val ISSUE = Issue.create(
            "DependencyApiCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            CUSTOM_CATEGORY,
            10,
            Severity.ERROR,
            Implementation(DependencyApiDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                //匹配要检查的dependencyApi
                val dependencyApi = lintConfigProvider.dependencyApiList.find {
                    LintMatcher.match(
                        it.triggerMethod,
                        it.triggerMethodRegex,
                        node.getQualifiedName()
                    )
                } ?: return

                var found = false
                var escape = false
                //必须传reference，不然DataFlowAnalyzer有bug，receiver不调用尴尬
                val reference = (node.receiver?.tryResolve() as? PsiVariable)
                val referenceList = if (reference == null) emptyList() else listOf(reference)
                val analyzer = object : DataFlowAnalyzer(setOf(node), referenceList) {
                    override fun receiver(call: UCallExpression) {
                        if (LintMatcher.match(
                                dependencyApi.dependencyMethod,
                                dependencyApi.dependencyMethodRegex,
                                call.getQualifiedName()
                            )
                        ) {
                            found = true
                        }
                    }

                    override fun returns(expression: UReturnExpression) {
                        escape = true
                    }

                    override fun argument(call: UCallExpression, reference: UElement) {
                        escape = true
                    }

                    override fun field(field: UElement) {
                        escape = true
                    }
                }

                node.getParentOfType<UMethod>(UMethod::class.java, true)?.accept(analyzer)
                //没找到并且没逃逸
                if (!found && !escape) {
                    context.report(ISSUE, context.getLocation(node), dependencyApi)
                }
            }
        }
    }
}