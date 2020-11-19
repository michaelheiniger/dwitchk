package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitch.ongoinggame.communication.CommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.WebsocketModule
import ch.qscqlmpa.dwitch.ongoinggame.game.GameModule
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomModule
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStoreModule
import ch.qscqlmpa.dwitch.ongoinggame.waitingroom.WaitingRoomModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameScreenBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostFragment
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    InGameStoreModule::class,
    WaitingRoomModule::class,
    GameRoomModule::class,
    GameModule::class,
    OngoingGameScreenBindingModule::class,
    OngoingGameViewModelBindingModule::class,
    MessageProcessorModule::class,
    CommunicationModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class,
    WebsocketModule::class
])
interface OngoingGameComponent {

    fun inject(activity: WaitingRoomActivity)
    fun inject(fragment: WaitingRoomHostFragment)
    fun inject(fragment: WaitingRoomGuestFragment)

    fun inject(activity: GameRoomActivity)
    fun inject(fragment: GameRoomHostFragment)
    fun inject(fragment: GameRoomGuestFragment)
    fun inject(fragment: PlayerDashboardFragment)

    val gameAdvertising: GameAdvertising
    val hostCommunicator: HostCommunicator
    val guestCommunicator: GuestCommunicator
}
