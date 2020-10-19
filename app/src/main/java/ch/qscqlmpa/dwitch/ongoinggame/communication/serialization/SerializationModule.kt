package ch.qscqlmpa.dwitch.ongoinggame.communication.serialization

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
abstract class SerializationModule {

    @Module
    companion object {

        @Singleton
        @JvmStatic
        @Provides
        fun provideKotlinxJson(): Json {
            return Json.Default
        }
    }
}