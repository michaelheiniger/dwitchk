package ch.qscqlmpa.dwitch

import androidx.navigation.NavHostController
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ui.home.MainActivityViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.navigation.ScreenNavigator
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@ActivityScope
@Subcomponent(
    modules = [MainActivityViewModelBindingModule::class]
)
abstract class MainActivityComponent : ScopedComponent() {

    @Named("home")
    abstract val mainViewModelFactory: ViewModelFactory

    abstract val screenNavigator: ScreenNavigator

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance navHostController: NavHostController): MainActivityComponent
    }

    abstract fun getInGameHostUiComponentFactory(): InGameHostUiComponent.Factory
    abstract fun getInGameGuestUiComponentFactory(): InGameGuestUiComponent.Factory
}
