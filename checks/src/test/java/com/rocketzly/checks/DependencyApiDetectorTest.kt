package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.rocketzly.checks.detector.DependencyApiDetector

/**
 * User: Rocket
 * Date: 2020/6/16
 * Time: 5:01 PM
 */
class DependencyApiDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector {
        return DependencyApiDetector()
    }

    override fun getIssues(): MutableList<Issue> {
        return mutableListOf(DependencyApiDetector.ISSUE)
    }

    fun test() {
        val testFile = kotlin(
            """
            package com.rocketzly.androidlint
            
            import java.lang.StringBuilder
            
            /**
             * User: Rocket
             * Date: 2020/6/16
             * Time: 3:42 PM
             */
            class Test {
            
                fun myTest(){
                    val i = 1
                    val sb = StringBuilder("12")
                    sb.append(1)
                    val j = i.dec()
                }
            }
        """.trimIndent()
        )

        lint()
            .files(
                testFile
            )
            .run()
            .expect("No warnings.")
    }


}