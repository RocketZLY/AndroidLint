package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.rocketzly.checks.detector.HandleExceptionDetector

/**
 * User: Rocket
 * Date: 2020/5/29
 * Time: 2:15 PM
 */
class HandleExceptionDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector {
        return HandleExceptionDetector()
    }

    override fun getIssues(): MutableList<Issue> {
        return mutableListOf(HandleExceptionDetector.ISSUE)
    }

    fun test() {
        val importJavaClass = java(
            """
            package android.graphics;
            public class Color {
                public static int parseColor(@Size(min=1) String colorString) {
                    throw new IllegalArgumentException("Unknown color");
                }
            }
        """.trimIndent()
        )
        val testFile = kotlin(
            """ 
             package com.rocketzly.androidlint

             import android.graphics.Color

             class ParseColorTest {

                    fun test() {
                        
                            Color.parseColor("#123123")
                        
                    }

             }
        """.trimIndent()
        )

        lint()
            .files(
                importJavaClass, testFile
            )
            .run()
            .expect("No warnings.")

    }
}