package com.rocketzly.gradle

import com.android.Version
import com.android.repository.Revision
import java.util.*

/**
 * User: Rocket
 * Date: 2021/11/11
 * Time: 4:40 下午
 */
object AgpApiFactory {

    /**
     * 获取IAgpApiProvider实现类，根据revision降序排序
     */
    private val agpApiProviderList by lazy {
        ServiceLoader.load(IAgpApiProvider::class.java, this::class.java.classLoader)
            .sortedByDescending { it.compatRevision }.toList()
    }

    /**
     * agp版本号x.y.z，api获取流程如下
     * 优先找大版本x和小版本y相同的
     * 其次找大版本x相同的
     * 都没有最后取最高版本的
     */
    private val provider: IAgpApiProvider by lazy {
        //当前agp版本
        val agpRevision = Revision.parseRevision(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        agpApiProviderList.firstOrNull {
            it.compatRevision.major == agpRevision.major && it.compatRevision.minor == agpRevision.minor
        } ?: agpApiProviderList.firstOrNull {
            it.compatRevision.major == agpRevision.major
        } ?: agpApiProviderList.first()
    }

    /**
     * 获取agp对应版本的apiProvider
     */
    fun getAgpApi(): IAgpApi {
        return provider.getAgpApi()
    }

}