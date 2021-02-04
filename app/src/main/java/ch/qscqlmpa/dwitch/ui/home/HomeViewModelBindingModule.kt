package ch.qscqlmpa.dwitch.ui.home

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameViewModel
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
    @ViewModelKey(NewGameViewModel::class)
    abstract fun bindNewGameActivityViewModel(viewModel: NewGameViewModel): ViewModel
}
