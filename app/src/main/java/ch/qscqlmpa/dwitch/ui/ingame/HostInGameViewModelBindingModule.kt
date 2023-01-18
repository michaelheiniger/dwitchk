package ch.qscqlmpa.dwitch.ui.ingame

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.ui.ingame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Named

@Suppress("unused")
@Module
abstract class HostInGameViewModelBindingModule {

    @Named("game")
    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomViewModel::class)
    internal abstract fun provideWaitingRoomViewModel(viewModel: WaitingRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WaitingRoomHostViewModel::class)
    internal abstract fun provideWaitingRoomHostViewModel(viewModel: WaitingRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomHostViewModel::class)
    internal abstract fun provideGameRoomHostViewModel(viewModel: GameRoomHostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRoomViewModel::class)
    abstract fun providePlayerDashboardViewModel(viewModel: GameRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionHostViewModel::class)
    abstract fun provideConnectionHostViewModel(viewModel: ConnectionHostViewModel): ViewModel
}
