package com.gbr.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val gbrDispatcher: GbrDispatchers)

enum class GbrDispatchers {
    Default,
    IO,
}
