package com.rocketzly.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.*
import com.android.tools.lint.checks.infrastructure.ProjectDescription
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rocketzly.checks.detector.ResourceNameDetector
import org.junit.Test

/**
 * User: Rocket
 * Date: 2021/10/11
 * Time: 7:42 下午
 */
class ResourceNameDetectorTest {

    private val shape = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<shape android:shape=\"rectangle\"\n" +
            "    xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
            "    <solid android:color=\"@color/mainButtonBackground\" />\n" +
            "    <corners android:radius=\"15dp\" />\n" +
            "</shape>"

    private val layout = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    android:layout_width=\"match_parent\"\n" +
            "    android:layout_height=\"match_parent\"\n" +
            "    android:orientation=\"vertical\" >\n" +
            "\n" +
            "    <include\n" +
            "        android:layout_width=\"wrap_content\"\n" +
            "        android:layout_height=\"wrap_content\"\n" +
            "        layout=\"@layout/layout2\" />\n" +
            "\n" +
            "    <Button\n" +
            "        android:id=\"@+id/button1\"\n" +
            "        android:layout_width=\"wrap_content\"\n" +
            "        android:layout_height=\"wrap_content\"\n" +
            "        android:text=\"Button\" />\n" +
            "\n" +
            "    <Button\n" +
            "        android:id=\"@+id/button2\"\n" +
            "        android:layout_width=\"wrap_content\"\n" +
            "        android:layout_height=\"wrap_content\"\n" +
            "        android:text=\"Button\" />\n" +
            "\n" +
            "</LinearLayout>\n"

    /**
     * 匹配的二进制资源
     */
    @Test
    fun matchingDrawableResource() {
        val project = ProjectDescription(
            image("src/main/res/drawable/bg_1111.png", 472, 290),
            image("src/main/res/drawable/shape_2222.png", 472, 290),
            xml(
                "src/main/res/drawable/bg_shape_r_15_e1e7ff.xml",
                shape
            ),
            //需要增加gradle()，这样才能正确解析src/main目录结构
            gradle("apply plugin: 'com.android.application'\n")
        )
        lint()
            .projects(project)
            .issues(ResourceNameDetector.ISSUE)
            .run()
            .expectClean()
    }

    /**
     * 不匹配的二进制资源
     */
    @Test
    fun mismatchingDrawableResource() {
        val project = ProjectDescription(
            image("src/main/res/drawable-mdpi/frame.png", 472, 290),
            xml(
                "src/main/res/drawable/search_r_15_e1e7ff.xml",
                shape
            ),
            //需要增加gradle()，这样才能正确解析src/main目录结构
            gradle("apply plugin: 'com.android.application'\n")
        )
        lint()
            .projects(project)
            .issues(ResourceNameDetector.ISSUE)
            .run()
            .expect(
                "src/main/res/drawable-mdpi/frame.png: Warning: drawable命名不符合 (bg|shape)_ 规则 [ResourceNameCheck]\n" +
                        "src/main/res/drawable/search_r_15_e1e7ff.xml: Warning: drawable命名不符合 (bg|shape)_ 规则 [ResourceNameCheck]\n" +
                        "0 errors, 2 warnings"
            )
    }

    /**
     * 匹配的xml资源
     */
    @Test
    fun matchingXmlResource() {
        val project = ProjectDescription(
            xml("res/layout/activity_main.xml", layout),
            xml("res/layout/dialog_main.xml", layout),
            xml("res/layout/item_main.xml", layout),
            xml("res/layout/view_main.xml", layout),
            xml("res/layout/page_main.xml", layout),
            //需要增加gradle()，这样才能正确解析src/main目录结构
            gradle("apply plugin: 'com.android.application'\n")
        )
        lint()
            .projects(project)
            .issues(ResourceNameDetector.ISSUE)
            .run()
            .expectClean()
    }

    /**
     * 不匹配的xml资源
     */
    @Test
    fun mismatchingXmlResource() {
        val project = ProjectDescription(
            xml("res/layout/search_main.xml", layout),
            xml("res/layout/home_main.xml", layout),
            //需要增加gradle()，这样才能正确解析src/main目录结构
            gradle("apply plugin: 'com.android.application'\n")
        )
        lint()
            .projects(project)
            .issues(ResourceNameDetector.ISSUE)
            .run()
            .expect(
                "res/layout/home_main.xml: Warning: layout命名不符合 (activity|dialog|item|view|page)_ 规则 [ResourceNameCheck]\n" +
                        "res/layout/search_main.xml: Warning: layout命名不符合 (activity|dialog|item|view|page)_ 规则 [ResourceNameCheck]\n" +
                        "0 errors, 2 warnings"
            )
    }
}