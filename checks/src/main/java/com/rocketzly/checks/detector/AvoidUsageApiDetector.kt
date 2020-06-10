package com.rocketzly.checks.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import org.jetbrains.uast.UCallExpression

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

    override fun getApplicableMethodNames(): List<String>? {
        return lintConfig.avoidUsageApi.getAvoidMethodNameList()
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val avoidUsageMethod = lintConfig.avoidUsageApi.getAvoidUsageMethodByName(method.name)
        if (avoidUsageMethod.inClass.isEmpty()) {//配置中不包含类信息直接报错
            context.report(
                ISSUE,
                context.getLocation(node),
                avoidUsageMethod.message
            )
            return
        }
        if (!context.evaluator.isMemberInClass(method, avoidUsageMethod.inClass)) {
            return
        }
        context.report(
            ISSUE,
            context.getLocation(node),
            avoidUsageMethod.message
        )
    }
}