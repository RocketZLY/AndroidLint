package com.rocketzly.gradle

import com.android.repository.Revision

/**
 * LintPlugin使用到agpApi的抽象，不同版本不同实现
 * User: Rocket
 * Date: 2021/11/11
 * Time: 4:32 下午
 */
interface IAgpApiProvider {

    /**
     * 当前provider兼容的agp版本号
     */
    val compatRevision: Revision

    /**
     * 获取apgApi实例
     */
    val agpApi: IAgpApi
}