package ch.qscqlmpa.dwitchcommunication.di

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module
internal abstract class SerializationModule {

    @Module
    companion object {

//        @Singleton
        @JvmStatic
        @Provides
        fun provideKotlinxJson(): Json {
            return Json.Default
        }
    }
}
