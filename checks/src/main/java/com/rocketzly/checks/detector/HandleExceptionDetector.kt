package com.rocketzly.checks.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UTryExpression
import org.jetbrains.uast.getParentOfType

/**
 * 调用指定API时，需要加try-catch处理指定类型的异常
 * User: Rocket
 * Date: 2020/5/28
 * Time: 4:00 PM
 */
class HandleExceptionDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "调用${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_HANDLE_EXCEPTION}指定API时，需要加try-catch处理指定类型的异常"
        val ISSUE = Issue.create(
            "HandleExceptionCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.SECURITY,
            10,
            Severity.ERROR,
            Implementation(HandleExceptionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return lintConfig.handleException.getHandleExceptionMethodNameList()
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val handleExceptionMethod =
            lintConfig.handleException.getHandleExceptionMethodByName(method.name)

        if (handleExceptionMethod.inClass.isNotEmpty()
            && !context.evaluator.isMemberInClass(method, handleExceptionMethod.inClass)
        ) {//不是当前要检查的类直接return
            return
        }

        val tryExpression: UTryExpression? =
            node.getParentOfType(UTryExpression::class.java)//获取try节点
        if (tryExpression == null) {
            context.report(ISSUE, context.getLocation(node), handleExceptionMethod.message)
            return
        }
        for (catch in tryExpression.catchClauses) {//拿到catch
            for (reference in catch.typeReferences) {//拿到异常类型
                if (context.evaluator.typeMatches(
                        reference.type,
                        handleExceptionMethod.exception
                    )
                ) {
                    return
                }
            }
        }
        context.report(ISSUE, context.getLocation(node), handleExceptionMethod.message)
    }
}