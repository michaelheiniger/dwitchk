package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostFacadeImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OnGoingGameQualifiers.CURRENT_ROOM
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OnGoingGameQualifiers.GAME_LOCAL_ID
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OnGoingGameQualifiers.HOST_PORT
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class OngoingGameModule(
    private val playerRole: PlayerRole,
    private val roomType: RoomType,
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    private val hostPort: Int,
    private val hostIpAddress: String,
    private val inGameStore: InGameStore,
    private val commComponent: CommunicationComponent
) {

    @OngoingGameScope
    @Provides
    fun provideCommServer(): CommServer {
        return commComponent.commServer
    }

    @OngoingGameScope
    @Provides
    fun provideCommClient(): CommClient {
        return commComponent.commClient
    }

    @OngoingGameScope
    @Provides
    fun provideConnectionStore(): ConnectionStore {
        return commComponent.connectionStore
    }

    @OngoingGameScope
    @Provides
    fun provideInGameStore(): InGameStore {
        return inGameStore
    }

    @OngoingGameScope
    @Provides
    fun providePlayerRole(): PlayerRole {
        return playerRole
    }

    @Named(GAME_LOCAL_ID)
    @OngoingGameScope
    @Provides
    fun provideGameLocalId(): Long {
        return gameLocalId
    }

    @Named(LOCAL_PLAYER_LOCAL_ID)
    @OngoingGameScope
    @Provides
    fun provideLocalPlayerLocalId(): Long {
        return localPlayerLocalId
    }

    @Named(CURRENT_ROOM)
    @OngoingGameScope
    @Provides
    fun provideCurrentRoom(): RoomType {
        return roomType
    }

    @Named(HOST_PORT)
    @OngoingGameScope
    @Provides
    fun provideHostPort(): Int {
        return hostPort
    }

    @Named(HOST_IP_ADDRESS)
    @OngoingGameScope
    @Provides
    fun provideHostIpAddress(): String {
        return hostIpAddress
    }

    @OngoingGameScope
    @Provides
    internal fun provideGameCommunicator(
        playerRole: PlayerRole,
        hostCommunicator: HostCommunicator,
        guestCommunicator: GuestCommunicator
    ): GameCommunicator {
        return when (playerRole) {
            PlayerRole.GUEST -> guestCommunicator
            PlayerRole.HOST -> hostCommunicator
        }
    }

    @OngoingGameScope
    @Provides
    internal fun provideHostFacade(
        hostCommunicationStateRepository: HostCommunicationStateRepository,
        communicator: HostCommunicator,
        gameAdvertising: GameAdvertising
    ): HostFacade {
        return HostFacadeImpl(hostCommunicationStateRepository, communicator, gameAdvertising)
    }

    @OngoingGameScope
    @Provides
    internal fun provideGuestFacade(communicator: GuestCommunicator): GuestFacade {
        return GuestFacadeImpl(communicator)
    }
}
