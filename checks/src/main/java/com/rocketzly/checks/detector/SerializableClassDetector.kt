package com.rocketzly.checks.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiClassType
import com.rocketzly.checks.CLASS_SERIALIZABLE
import com.rocketzly.checks.LintMatcher
import com.rocketzly.checks.report
import org.jetbrains.uast.UClass

/**
 * 实现了Serializable接口的类，引用类型成员变量也必须要实现Serializable接口
 * User: Rocket
 * Date: 2020/5/27
 * Time: 4:57 PM
 */
class SerializableClassDetector : BaseDetector(), Detector.UastScanner {

    companion object {
        private const val REPORT_MESSAGE = "该对象必须要实现Serializable接口，因为外部类实现了Serializable接口"
        val ISSUE = Issue.create(
            "SerializableClassCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(SerializableClassDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun applicableSuperClasses(): List<String>? {
        return listOf(CLASS_SERIALIZABLE)
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        for (field in declaration.fields) {
            //字段是引用类型，并且可以拿到该class
            val psiClass = (field.type as? PsiClassType)?.resolve() ?: continue
            if (!LintMatcher.matchClass(
                    lintConfig.serializableConfig,
                    psiClass
                )
            ) {
                return
            }
            if (!context.evaluator.implementsInterface(psiClass, CLASS_SERIALIZABLE, true)) {
                context.report(
                    ISSUE,
                    context.getLocation(field.typeReference!!),
                    lintConfig.serializableConfig
                )
            }
        }
    }

}