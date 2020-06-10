package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.rocketzly.checks.detector.AvoidUsageApiDetector

/**
 * User: Rocket
 * Date: 2020/6/9
 * Time: 4:53 PM
 */
class AvoidUsageApiDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector {
        return AvoidUsageApiDetector()
    }

    override fun getIssues(): MutableList<Issue> {
        return mutableListOf(AvoidUsageApiDetector.ISSUE)
    }

    fun testAvoidUsageMethod() {
        val importActivityClass = java(
            """
            package android.content;
            
            public class ContextWrapper  {
                public void getSharedPreferences(String name, int mode) {
                    
                }
            }
            """.trimIndent()
        )

        val testClass = kotlin(
            """
            package com.rocketzly.androidlint
            
            import android.content.ContextWrapper
            
            /**
             * User: Rocket
             * Date: 2020/6/9
             * Time: 5:28 PM
             */
            class Test {
                
                fun test(context: ContextWrapper){
                    context.getSharedPreferences("123",1)
                }
            }
                """.trimIndent()
        )

        lint()
            .files(
                importActivityClass, testClass
            )
            .run()
            .expect("")
    }
}