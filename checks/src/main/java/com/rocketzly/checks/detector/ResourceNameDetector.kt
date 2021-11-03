package com.rocketzly.checks.detector

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.CUSTOM_CATEGORY
import com.rocketzly.checks.config.LintConfigProvider
import com.rocketzly.checks.config.LintParserKey
import com.rocketzly.checks.matcher.LintMatcher
import com.rocketzly.checks.report
import java.util.*

/**
 * User: Rocket
 * Date: 2020/6/19
 * Time: 10:55 AM
 */
//BinaryResourceScanner扫描位图png、jpg之类
//XmlScanner扫描xml文件layout、drawable、shape之类
class ResourceNameDetector : BaseDetector(), BinaryResourceScanner, XmlScanner {

    companion object {
        private const val REPORT_MESSAGE =
            "资源命名请按${LintConfigProvider.CONFIG_FILE_NAME}中${LintParserKey.KEY_RESOURCE_NAME}配置的规则"
        val ISSUE = Issue.create(
            "ResourceNameCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            CUSTOM_CATEGORY,
            10,
            Severity.ERROR,
            Implementation(
                ResourceNameDetector::class.java,
                EnumSet.of(Scope.RESOURCE_FILE, Scope.BINARY_RESOURCE_FILE)
            )
        )
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return true
    }

    //位图和xml文件都会走该方法，统一在此处检查命名
    override fun beforeCheckFile(context: Context) {
        //只检查资源或者xml
        if (context !is ResourceContext) {
            return
        }

        //拿到对应的资源命名数据
        val resourceName = when (context.resourceFolderType) {
            //图片png、shape
            ResourceFolderType.DRAWABLE -> lintConfigProvider.resourceName.drawable
            //布局
            ResourceFolderType.LAYOUT -> lintConfigProvider.resourceName.layout
            else -> null
        } ?: return

        if (resourceName.name.isEmpty() && resourceName.nameRegex.isEmpty()) {
            return
        }

        val fileName = getBaseName(context.file.name)
        if (!LintMatcher.matchFileName(resourceName, fileName)) {
            context.report(ISSUE, Location.create(context.file), resourceName)
        }
    }
}