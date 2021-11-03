package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rocketzly.checks.detector.AvoidUsageApiDetector
import org.junit.Test

/**
 * User: Rocket
 * Date: 2020/6/9
 * Time: 4:53 PM
 */
class AvoidUsageApiDetectorTest {

    /**
     * 避免使用的方法
     */
    @Test
    fun avoidUsageMethod() {
        val importLogClass = java(
            """
            package android.util;
            public final class Log {
                public static void i(String tag, String msg) {
                    
                }
            }
            """
        ).indented()

        val testCode = kotlin(
            """
                package com.rocketzly.androidlint

                import android.util.Log

                /**
                 * User: Rocket
                 * Date: 2020/6/12
                 * Time: 11:37 AM
                 */
                class Test {
                    fun test(){
                        Log.i("zhuliyuan", "123")
                    }
                }
            """
        ).indented()
        lint()
            .files(
                importLogClass, testCode
            )
            .issues(AvoidUsageApiDetector.ISSUE)
            .run()
            .expect(
                "src/com/rocketzly/androidlint/Test.kt:12: Error: 禁止直接使用android.util.Log，必须使用统一工具类xxxLog [AvoidUsageApiCheck]\n" +
                        "        Log.i(\"zhuliyuan\", \"123\")\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "1 errors, 0 warnings"
            )
    }

    /**
     * 避免创建的类
     */
    @Test
    fun avoidUsageConstruction() {
        val testClass = java(
            """
            package com.rocketzly.checks;
            
            /**
             * User: Rocket
             * Date: 2020/6/12
             * Time: 5:25 PM
             */
            public class Test {
                public void test(){
                    new Thread();
                }
            }
            """.trimIndent()
        )

        lint()
            .files(
                testClass
            )
            .issues(AvoidUsageApiDetector.ISSUE)
            .run()
            .expect(
                "src/com/rocketzly/checks/Test.java:10: Error: 禁止直接使用new Thread()创建线程，建议使用xxxUtils做线程操作 [AvoidUsageApiCheck]\n" +
                        "        new Thread();\n" +
                        "        ~~~~~~~~~~~~\n" +
                        "1 errors, 0 warnings"
            )
    }

    /**
     * 避免继承的类
     */
    @Test
    fun testAvoidInheritClass() {
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
            .issues(AvoidUsageApiDetector.ISSUE)
            .run()
            .expect(
                "src/com/rocketzly/androidlint/MainActivity.kt:5: Warning: 避免直接继承Activity，建议继承xxxActivity [AvoidUsageApiCheck]\n" +
                        "class MainActivity : AppCompatActivity() {\n" +
                        "^\n" +
                        "0 errors, 1 warnings"
            )
    }
}