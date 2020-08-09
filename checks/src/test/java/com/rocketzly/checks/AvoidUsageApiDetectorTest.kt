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

    /**
     * 测试通过名字匹配的方法
     */
    fun testAvoidUsageMethodByName() {
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
            .expect("No warnings.")
    }

    /**
     * 测试通过正则匹配的方法
     */
    fun testAvoidUsageMethodByNameRegex() {
        val importLogClass = java(
            """
            package android.util;
            public final class Log {
                public static void i(String tag, String msg) {
                    
                }
            }
            """.trimIndent()
        )

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
            """.trimIndent()
        )
        lint()
            .files(
                importLogClass, testCode
            )
            .run()
            .expect("No warnings.")
    }

    fun testAvoidUsageConstruction() {
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
            .run()
            .expect("No warnings.")
    }

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
            .run()
            .expect("No warnings.")
    }
}