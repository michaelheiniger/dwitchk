package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
internal abstract class SerializationModule {

    companion object {

        @GameScope
        @Provides
        fun provideKotlinxJson(): Json {
            return Json.Default
        }
    }
}
