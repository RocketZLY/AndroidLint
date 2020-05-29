package com.rocketzly.checks.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import com.rocketzly.checks.CLASS_COLOR
import com.rocketzly.checks.CLASS_ILLEGAL_ARGUMENT_EXCEPTION
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UTryExpression
import org.jetbrains.uast.getParentOfType

/**
 * Color#parseColor()方法解析颜色的时候必须要处理IllegalArgumentException异常，
 * 避免后台下发颜色格式不正确造成crash。
 * User: Rocket
 * Date: 2020/5/28
 * Time: 4:00 PM
 */
class ParseColorDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "Color.parseColor()方法必须处理IllegalArgumentException异常，避免后台下发颜色格式不正确造成crash"
        val ISSUE = Issue.create(
            "ColorParseCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.SECURITY,
            10,
            Severity.ERROR,
            Implementation(ParseColorDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("parseColor")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!context.evaluator.isMemberInClass(method, CLASS_COLOR)) return
        val tryExpression: UTryExpression? =
            node.getParentOfType(UTryExpression::class.java)//获取try节点
        if (tryExpression == null) {
            context.report(ISSUE, context.getLocation(node), REPORT_MESSAGE)
            return
        }
        for (catch in tryExpression.catchClauses) {//拿到catch
            for (reference in catch.typeReferences) {//拿到异常类型
                if (context.evaluator.typeMatches(reference.type, CLASS_ILLEGAL_ARGUMENT_EXCEPTION)) {
                    return
                }
            }
        }
        context.report(ISSUE, context.getLocation(node), REPORT_MESSAGE)
    }
}