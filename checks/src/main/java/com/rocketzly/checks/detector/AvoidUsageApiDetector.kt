package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.LintMatcher
import com.rocketzly.checks.report
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.util.isConstructorCall
import org.jetbrains.uast.util.isMethodCall

/**
 * 避免使用api检测器（目前可以检测方法调用、类创建、实现或者继承）
 * User: Rocket
 * Date: 2020/6/9
 * Time: 4:35 PM
 */
class AvoidUsageApiDetector : BaseDetector(), Detector.UastScanner {

    companion object {
        private const val REPORT_MESSAGE =
            "避免使用${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_AVOID_USAGE_API}配置的api"
        val ISSUE = Issue.create(
            "AvoidUsageApiCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(AvoidUsageApiDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java, UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                if (node.isMethodCall()) {
                    checkMethodCall(context, node)
                } else if (node.isConstructorCall()) {
                    checkConstructorCall(context, node)
                }
            }

            override fun visitClass(node: UClass) {
                checkInheritClass(context, node)
            }
        }
    }

    private fun checkMethodCall(context: JavaContext, node: UCallExpression) {
        lintConfig.avoidUsageApi.method.forEach {
            if (LintMatcher.matchMethod(it, node)) {
                context.report(ISSUE, context.getLocation(node), it)
                return
            }
        }
    }

    private fun checkConstructorCall(context: JavaContext, node: UCallExpression) {
        lintConfig.avoidUsageApi.construction.forEach {
            if (LintMatcher.matchConstruction(it, node)) {
                context.report(ISSUE, context.getLocation(node), it)
                return
            }
        }

    }

    private fun checkInheritClass(context: JavaContext, node: UClass) {
        lintConfig.avoidUsageApi.inherit.forEach { avoidInheritClass ->
            if (LintMatcher.matchInheritClass(
                    avoidInheritClass,
                    node
                )
            ) {
                context.report(
                    ISSUE,
                    context.getLocation(node as UElement),
                    avoidInheritClass
                )
                return
            }
        }
    }

}