package com.rocketzly.gradle.v40

import com.android.repository.Revision
import com.google.auto.service.AutoService
import com.rocketzly.gradle.IAgpApiProvider
import com.rocketzly.gradle.api.IAgpApi

/**
 * User: Rocket
 * Date: 2021/11/16
 * Time: 2:24 下午
 */
@AutoService(IAgpApiProvider::class)
class AgpApiProviderV40 : IAgpApiProvider {
    override val compatRevision: Revision = Revision(4, 0, 0)

    override val agpApi: IAgpApi by lazy {
        AgpApiV40()
    }
}