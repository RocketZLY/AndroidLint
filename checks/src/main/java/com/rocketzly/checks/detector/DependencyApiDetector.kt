package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.LintMatcher
import com.rocketzly.checks.config.bean.DependencyApi
import com.rocketzly.checks.getQualifiedName
import com.rocketzly.checks.report
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * 有依赖关系api
 * 目前检查开始条件是[DependencyApi.triggerMethod]方法被调用，
 * 如果满足开始条件则检查[DependencyApi.triggerMethod]后面的方法，
 * 有没有调用[DependencyApi.dependencyMethod]方法如果没调用则report。
 *
 * 警告：⚠️目前只能检查[DependencyApi.triggerMethod]在方法中被调用的情况，
 * 其次由于无法区分类的实例，如果同一个方法中后面有其他的实例调用了
 * [DependencyApi.dependencyMethod]也会认为当前实例调用了依赖方法，不在report（目前没找到解决办法😂）
 *
 * User: Rocket
 * Date: 2020/6/16
 * Time: 10:09 AM
 */
class DependencyApiDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "使用${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_DEPENDENCY_API}配置的api时必须调用dependencyMethod方法"
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
                    LintMatcher.match(null, it.triggerMethod, node.getQualifiedName())
                } ?: return

                //拿到外层方法
                val outMethod =
                    node.getParentOfType<UAnnotationMethod>(UAnnotationMethod::class.java, true)
                        ?: return
                val dependencyApiFinder = DependencyApiFinder(node, dependencyApi)
                outMethod.accept(dependencyApiFinder)//检查outMethod内是否有调用dependency_method
                if (dependencyApiFinder.isFound()) {
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

        private var seenTarget = false
        private var found = false

        override fun visitCallExpression(node: UCallExpression): Boolean {
            if (target == node) {
                seenTarget = true
                return super.visitCallExpression(node)
            }
            if (seenTarget &&
                LintMatcher.match(null, dependencyApi.dependencyMethod, node.getQualifiedName())
            ) {
                found = true
            }
            return super.visitCallExpression(node)
        }

        fun isFound() = found
    }
}