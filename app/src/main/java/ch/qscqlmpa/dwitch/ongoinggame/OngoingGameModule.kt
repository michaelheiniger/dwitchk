package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.CURRENT_ROOM
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.GAME_LOCAL_ID
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_PORT
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
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
    private val hostIpAddress: String
) {

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
    fun provideGameCommunicator(playerRole: PlayerRole, hostCommunicator: HostCommunicator, guestCommunicator: GuestCommunicator): GameCommunicator {
        return when (playerRole) {
            PlayerRole.GUEST -> guestCommunicator
            PlayerRole.HOST -> hostCommunicator
        }
    }

    @OngoingGameScope
    @Provides
    fun provideGuestFacade(communicator: GuestCommunicator): GuestFacade {
        return GuestFacadeImpl(communicator)
    }
}
