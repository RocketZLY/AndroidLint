package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.config.LintRuleMatcher
import com.rocketzly.checks.config.bean.DependencyApi
import com.rocketzly.checks.report
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * 有依赖关系api
 * User: Rocket
 * Date: 2020/6/16
 * Time: 10:09 AM
 */
class DependencyApiDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "避免使用${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_DEPENDENCY_API}配置的api"
        val ISSUE = Issue.create(
            "DependencyApiCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
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
                val dependencyApi = lintConfig.dependencyApiList.find {
                    LintRuleMatcher.match(it.conditionMethod, null, node.methodName)
                            && LintRuleMatcher.match(
                        it.clazz,
                        null,
                        node.classReference.getQualifiedName()
                    )
                } ?: return

                //拿到外层方法
                val outMethod =
                    node.getParentOfType<UAnnotationMethod>(UAnnotationMethod::class.java, true)
                        ?: return

                val dependencyApiFinder = DependencyApiFinder(node, dependencyApi)
                outMethod.accept(dependencyApiFinder)//检查outMethod内是否有调用dependency_method
                if (dependencyApiFinder.found) {
                    return
                }
                context.report(ISSUE, context.getLocation(node), dependencyApi)
            }

        }
    }


    class DependencyApiFinder(
        private val target: UCallExpression,
        private val dependencyApi: DependencyApi
    ) : AbstractUastVisitor() {

        var seenTarget = false
        var found = false

        override fun visitCallExpression(node: UCallExpression): Boolean {
            if (target == node) {
                seenTarget = true
                return super.visitCallExpression(node)
            }

            if (seenTarget &&
                LintRuleMatcher.match(dependencyApi.dependencyMethod, null, node.methodName) &&
                LintRuleMatcher.match(
                    dependencyApi.clazz,
                    null,
                    node.classReference.getQualifiedName()
                )
            ) {
                found = true
            }
            return super.visitCallExpression(node)
        }

        fun isFound() = found
    }
}