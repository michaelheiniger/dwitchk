package ch.qscqlmpa.dwitch.ui.ingame

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Named

@Suppress("unused")
@Module
abstract class GuestInGameViewModelBindingModule {

    @Named("game")
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomViewModel::class)
    internal abstract fun bindWaitingRoomActivityViewModel(viewModel: WaitingRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomGuestViewModel::class)
    internal abstract fun bindWaitingRoomGuestViewModel(viewModel: WaitingRoomGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomGuestViewModel::class)
    internal abstract fun bindGameRoomGuestViewModel(viewModel: GameRoomGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomViewModel::class)
    abstract fun bindPlayerDashboardViewModel(viewModel: GameRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionGuestViewModel::class)
    abstract fun bindConnectionGuestViewModel(viewModel: ConnectionGuestViewModel): ViewModel
}
