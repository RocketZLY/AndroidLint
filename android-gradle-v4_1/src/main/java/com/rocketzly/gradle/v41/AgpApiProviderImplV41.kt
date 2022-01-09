package com.rocketzly.gradle.v41

import com.android.repository.Revision
import com.google.auto.service.AutoService
import com.rocketzly.gradle.IAgpApi
import com.rocketzly.gradle.IAgpApiProvider

/**
 * User: Rocket
 * Date: 2021/11/14
 * Time: 4:22 下午
 */
@AutoService(IAgpApiProvider::class)
class AgpApiProviderImplV41 : IAgpApiProvider {

    override val compatRevision: Revision = Revision(4, 1, 0)

    override fun getAgpApi(): IAgpApi = AgpApiV41

}