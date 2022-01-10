package com.rocketzly.gradle.v36

import com.android.repository.Revision
import com.google.auto.service.AutoService
import com.rocketzly.gradle.IAgpApi
import com.rocketzly.gradle.IAgpApiProvider

/**
 * User: Rocket
 * Date: 2021/11/16
 * Time: 2:51 下午
 */
@AutoService(IAgpApiProvider::class)
class AgpApiProviderV36 : IAgpApiProvider {
    override val compatRevision: Revision = Revision(3, 6, 0)

    override val agpApi: IAgpApi by lazy {
        AgpApiV36()
    }
}