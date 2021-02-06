package ch.qscqlmpa.dwitch.ui.home

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameViewModel
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameViewModel
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Named

@Module
abstract class HomeViewModelBindingModule {

    @Named("home")
    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HostNewGameViewModel::class)
    abstract fun bindHostNewGameActivityViewModel(viewModel: HostNewGameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(JoinNewGameViewModel::class)
    abstract fun bindJoinNewGameActivityViewModel(viewModel: JoinNewGameViewModel): ViewModel
}
