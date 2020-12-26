package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.game.DwitchEngineFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchstore.store.Store
import dagger.Module
import dagger.Provides

@Module
class GameModule(
    private val store: Store
) {

    @GameScope
    @Provides
    fun provideStore(): Store {
        return store
    }

    @GameScope
    @Provides
    internal fun provideDwitchEngineFactory(): DwitchEngineFactory {
        return ProdDwitchEngineFactory()
    }
}