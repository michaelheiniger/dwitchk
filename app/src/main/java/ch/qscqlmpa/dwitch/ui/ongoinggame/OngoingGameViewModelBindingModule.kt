package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameUiScope
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Named

@Suppress("unused")
@Module
abstract class OngoingGameViewModelBindingModule {

    @Named("ongoingGame")
    @OngoingGameUiScope
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
    @ViewModelKey(WaitingRoomHostViewModel::class)
    internal abstract fun bindWaitingRoomHostViewModel(viewModel: WaitingRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomViewModel::class)
    abstract fun bindGameRoomActivityViewModel(viewModel: GameRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomGuestViewModel::class)
    internal abstract fun bindGameRoomGuestViewModel(viewModel: GameRoomGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomHostViewModel::class)
    internal abstract fun bindGameRoomHostViewModel(viewModel: GameRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerDashboardViewModel::class)
    abstract fun bindPlayerDashboardViewModel(viewModel: PlayerDashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionGuestViewModel::class)
    abstract fun bindConnectionGuestViewModel(viewModel: ConnectionGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionHostViewModel::class)
    abstract fun bindConnectionHostViewModel(viewModel: ConnectionHostViewModel): ViewModel
}
