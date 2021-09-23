package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiser
import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.di.OnGoingGameQualifiers
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacadeImpl
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InGameHostModule(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    private val gameAdvertiser: GameAdvertiser,
    private val inGameStore: InGameStore,
    private val commServer: CommServer,
    private val connectionStore: ConnectionStore
) {

    @InGameScope
    @Provides
    internal fun provideHostFacade(facade: InGameHostFacadeImpl): InGameHostFacade {
        return facade
    }

    @InGameScope
    @Provides
    internal fun provideGameAdvertisingFacade(schedulerFactory: SchedulerFactory): GameAdvertisingFacade {
        return GameAdvertisingFacadeImpl(inGameStore, gameAdvertiser, schedulerFactory)
    }

    @InGameScope
    @Provides
    fun provideCommServer(): CommServer {
        return commServer
    }

    @InGameScope
    @Provides
    fun provideConnectionStore(): ConnectionStore {
        return connectionStore
    }

    @InGameScope
    @Provides
    fun provideInGameStore(): InGameStore {
        return inGameStore
    }

    @InGameScope
    @Provides
    fun providePlayerRole(): PlayerRole {
        return PlayerRole.HOST
    }

    @Named(OnGoingGameQualifiers.GAME_LOCAL_ID)
    @InGameScope
    @Provides
    fun provideGameLocalId(): Long {
        return gameLocalId
    }

    @Named(OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID)
    @InGameScope
    @Provides
    fun provideLocalPlayerLocalId(): Long {
        return localPlayerLocalId
    }

    @Provides
    internal fun provideGameCommunicator(
        hostCommunicator: HostCommunicator
    ): GameCommunicator {
        return hostCommunicator
    }

    @Provides
    internal fun provideCommunicationStateRepository(hostCommunicationStateRepository: HostCommunicationStateRepository): CommunicationStateRepository {
        return hostCommunicationStateRepository
    }
}
