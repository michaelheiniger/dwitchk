package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ScopedComponent
import ch.qscqlmpa.dwitch.ui.ingame.HostInGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@InGameUiScope
@Subcomponent(
    modules = [
        HostInGameViewModelBindingModule::class
    ]
)
abstract class InGameHostUiComponent : ScopedComponent() {

    @Named("game")
    abstract val viewModelFactory: ViewModelFactory

    @Suppress("LongParameterList")
    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance gameAdvertisingFacade: GameAdvertisingFacade,
            @BindsInstance hostCommunicationFacade: HostCommunicationFacade,
            @BindsInstance waitingRoomFacade: WaitingRoomFacade,
            @BindsInstance waitingRoomHostFacade: WaitingRoomHostFacade,
            @BindsInstance inGameHostFacade: InGameHostFacade,
            @BindsInstance playerFacade: PlayerFacade
        ): InGameHostUiComponent
    }
}
