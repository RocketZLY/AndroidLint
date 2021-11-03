package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rocketzly.checks.detector.DependencyApiDetector
import org.junit.Test

/**
 * User: Rocket
 * Date: 2020/6/16
 * Time: 5:01 PM
 */
class DependencyApiDetectorTest {

    /**
     * 没有调用触发方法
     */
    @Test
    fun noCallTriggerMethod() {
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
                    val j = i.dec()
                }
            }
        """
        ).indented()

        lint()
            .files(
                testFile
            )
            .issues(DependencyApiDetector.ISSUE)
            .run()
            .expect(
                "No warnings."
            )
    }

    /**
     * 没有调用依赖方法
     */
    @Test
    fun noCallDependenceMethod() {
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
        """
        ).indented()

        lint()
            .files(
                testFile
            )
            .issues(DependencyApiDetector.ISSUE)
            .run()
            .expect(
                "src/com/rocketzly/androidlint/Test.kt:15: Warning: StringBuilder调用append后必须调用toString [DependencyApiCheck]\n" +
                        "        sb.append(1)\n" +
                        "        ~~~~~~~~~~~~\n" +
                        "0 errors, 1 warnings"
            )
    }

    /**
     * 调用了依赖方法
     */
    @Test
    fun callDependenceMethod() {
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
                    sb.append(2)
                    sb.toString()
                }
            }
        """
        ).indented()

        lint()
            .files(
                testFile
            )
            .issues(DependencyApiDetector.ISSUE)
            .run()
            .expect("No warnings.")
    }

    /**
     * 引用逃逸
     */
    @Test
    fun referenceEscape() {
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
                    tryEscape(sb)
                }

                private fun tryEscape(sb : StringBuilder){
                    
                }
            }
        """
        ).indented()

        lint()
            .files(
                testFile
            )
            .issues(DependencyApiDetector.ISSUE)
            .run()
            .expect(
                "No warnings."
            )
    }
}


