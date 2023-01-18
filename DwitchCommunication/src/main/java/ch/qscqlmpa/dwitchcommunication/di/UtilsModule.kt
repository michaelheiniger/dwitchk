package ch.qscqlmpa.dwitchcommunication.di

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
object UtilsModule {

    @CommunicationScope
    @Provides
    fun provideJsonSerializer(): Json {
        return Json.Default
    }
}
