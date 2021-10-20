package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ScopedComponent
import ch.qscqlmpa.dwitch.ui.ingame.GuestInGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@InGameUiScope
@Subcomponent(
    modules = [
        GuestInGameViewModelBindingModule::class
    ]
)
abstract class InGameGuestUiComponent : ScopedComponent() {

    @Named("game")
    abstract val viewModelFactory: ViewModelFactory

    @Suppress("LongParameterList")
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance guestCommunicationFacade: GuestCommunicationFacade,
            @BindsInstance waitingRoomFacade: WaitingRoomFacade,
            @BindsInstance waitingRoomGuestFacade: WaitingRoomGuestFacade,
            @BindsInstance inGameGuestFacade: InGameGuestFacade,
            @BindsInstance playerFacade: PlayerFacade
        ): InGameGuestUiComponent
    }
}
