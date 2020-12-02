package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module
internal abstract class SerializationModule {

    @Module
    companion object {

        @GameScope
        @JvmStatic
        @Provides
        fun provideKotlinxJson(): Json {
            return Json.Default
        }
    }
}