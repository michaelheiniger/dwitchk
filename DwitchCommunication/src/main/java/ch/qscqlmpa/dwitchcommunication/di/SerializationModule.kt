package ch.qscqlmpa.dwitchcommunication.di

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
internal class SerializationModule {

    @Provides
    fun provideKotlinxJson(): Json {
        return Json.Default
    }
}
