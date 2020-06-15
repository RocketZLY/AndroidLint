package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.rocketzly.checks.detector.AvoidInheritClassDetector

/**
 * User: Rocket
 * Date: 2020/6/15
 * Time: 3:00 PM
 */
class AvoidInheritClassDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector {
        return AvoidInheritClassDetector()
    }

    override fun getIssues(): MutableList<Issue> {
        return mutableListOf(AvoidInheritClassDetector.ISSUE)
    }

    fun test() {
        val importFile = kotlin(
            """
                package androidx.appcompat.app;
                open class AppCompatActivity{
                    
                }
            """.trimIndent()
        )


        val testFile = kotlin(
            """
            package com.rocketzly.androidlint

            import androidx.appcompat.app.AppCompatActivity

            class MainActivity : AppCompatActivity() {

            }

        """.trimIndent()
        )

        lint()
            .files(importFile, testFile)
            .run()
            .expect("")
    }
}