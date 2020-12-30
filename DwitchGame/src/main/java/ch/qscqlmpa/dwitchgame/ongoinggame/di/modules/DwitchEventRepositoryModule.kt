package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent.DwitchEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent.DwitchEventRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class DwitchEventRepositoryModule {

    @OngoingGameScope
    @Binds
    internal abstract fun bindDwitchEventRepository(repository: DwitchEventRepositoryImpl): DwitchEventRepository
}