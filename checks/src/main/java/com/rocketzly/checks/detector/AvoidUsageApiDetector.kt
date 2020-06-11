package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.getQualifiedName
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * 避免使用api检测器
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
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                checkMethod(context, node)
            }
        }
    }

    private fun checkMethod(context: JavaContext, node: UCallExpression) {
        val qualifiedName = node.getQualifiedName()
        lintConfig.avoidUsageApi.method.forEach {
            if (it.name.isNotEmpty() && it.name == qualifiedName) {//优先匹配name
                context.report(ISSUE, context.getLocation(node), it.message)
                return
            }
            //再匹配nameRegex
            if (it.nameRegex.isNotEmpty()) {

            }
        }
    }
}