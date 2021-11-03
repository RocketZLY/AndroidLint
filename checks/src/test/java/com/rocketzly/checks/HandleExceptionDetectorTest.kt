package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rocketzly.checks.detector.HandleExceptionDetector
import org.junit.Test

/**
 * User: Rocket
 * Date: 2020/5/29
 * Time: 2:15 PM
 */
class HandleExceptionDetectorTest {

    private val importJavaClass = java(
        """
            package android.graphics;
            public class Color {
                public static int parseColor(String colorString) {
                    throw new IllegalArgumentException("Unknown color");
                }
            }
        """
    ).indented()

    //没有try
    @Test
    fun noTry() {
        val testFile = kotlin(
            """
             package com.rocketzly.androidlint

             import android.graphics.Color

             class ParseColorTest {
                    fun test() {
                        Color.parseColor("#123123")
                    }
             }
        """
        ).indented()

        lint()
            .files(
                importJavaClass, testFile
            )
            .issues(HandleExceptionDetector.ISSUE)
            .run()
            .expect("src/com/rocketzly/androidlint/ParseColorTest.kt:7: Error: Color.parseColor需要加try-catch处理IllegalArgumentException异常 [HandleExceptionCheck]\n" +
                    "           Color.parseColor(\"#123123\")\n" +
                    "           ~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "1 errors, 0 warnings")
    }

    /**
     * try 异常本身
     */
    @Test
    fun tryCurrentException() {
        val testFile = kotlin(
            """
             package com.rocketzly.androidlint

             import android.graphics.Color

             class ParseColorTest {
                    fun test() {
                        try {
                            Color.parseColor("#123123")
                        }catch (e: IllegalArgumentException){
                            e.printStackTrace()
                        }
                    }
             }
        """
        ).indented()

        lint()
            .files(
                importJavaClass, testFile
            )
            .issues(HandleExceptionDetector.ISSUE)
            .run()
            .expect("No warnings.")
    }

    /**
     * try父类异常
     */
    @Test
    fun tryParentException() {
        val testFile = kotlin(
            """
             package com.rocketzly.androidlint

             import android.graphics.Color

             class ParseColorTest {
                    fun test() {
                        try {
                            Color.parseColor("#123123")
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
             }
        """
        ).indented()

        lint()
            .files(
                importJavaClass, testFile
            )
            .issues(HandleExceptionDetector.ISSUE)
            .run()
            .expect("No warnings.")
    }
}