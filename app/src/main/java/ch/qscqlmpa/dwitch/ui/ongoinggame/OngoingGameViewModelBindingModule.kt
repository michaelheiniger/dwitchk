package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.service.OngoingGameScope
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

@Module
abstract class OngoingGameViewModelBindingModule {

    @Named("ongoingGame")
    @OngoingGameScope
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomViewModel::class)
    abstract fun bindWaitingRoomActivityViewModel(viewModel: WaitingRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomGuestViewModel::class)
    abstract fun bindWaitingRoomGuestViewModel(viewModel: WaitingRoomGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomHostViewModel::class)
    abstract fun bindWaitingRoomHostViewModel(viewModel: WaitingRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomViewModel::class)
    abstract fun bindGameRoomActivityViewModel(viewModel: GameRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomGuestViewModel::class)
    abstract fun bindGameRoomGuestViewModel(viewModel: GameRoomGuestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomHostViewModel::class)
    abstract fun bindGameRoomHostViewModel(viewModel: GameRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerDashboardViewModel::class)
    abstract fun bindPlayerDashboardViewModel(viewModel: PlayerDashboardViewModel): ViewModel
}
