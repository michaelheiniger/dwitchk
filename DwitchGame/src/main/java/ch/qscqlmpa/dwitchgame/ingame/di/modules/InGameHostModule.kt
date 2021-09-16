package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.OnGoingGameQualifiers
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InGameHostModule(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    private val inGameStore: InGameStore,
    private val commServer: CommServer,
    private val connectionStore: ConnectionStore
) {

    @OngoingGameScope
    @Provides
    internal fun provideHostFacade(facade: InGameHostFacadeImpl): InGameHostFacade {
        return facade
    }

    @OngoingGameScope
    @Provides
    fun provideCommServer(): CommServer {
        return commServer
    }

    @OngoingGameScope
    @Provides
    fun provideConnectionStore(): ConnectionStore {
        return connectionStore
    }

    @OngoingGameScope
    @Provides
    fun provideInGameStore(): InGameStore {
        return inGameStore
    }

    @OngoingGameScope
    @Provides
    fun providePlayerRole(): PlayerRole {
        return PlayerRole.HOST
    }

    @Named(OnGoingGameQualifiers.GAME_LOCAL_ID)
    @OngoingGameScope
    @Provides
    fun provideGameLocalId(): Long {
        return gameLocalId
    }

    @Named(OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID)
    @OngoingGameScope
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