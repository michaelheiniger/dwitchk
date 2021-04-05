package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppComponent
import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.app.ApplicationModule
import ch.qscqlmpa.dwitch.app.SchedulersModule
import ch.qscqlmpa.dwitch.ongoinggame.OnGoingGameUiModule
import ch.qscqlmpa.dwitch.ongoinggame.TestOngoingGameUiComponent
import ch.qscqlmpa.dwitch.service.ServiceManagerModule
import ch.qscqlmpa.dwitch.ui.home.HomeActivityBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.Component
import dagger.android.AndroidInjectionModule

@AppScope
@Component(
    dependencies = [GameComponent::class],
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        HomeActivityBindingModule::class,
        HomeViewModelBindingModule::class,
        ServiceManagerModule::class,
        SchedulersModule::class,
    ]
)
interface TestAppComponent : AppComponent {

    val mainActivityViewModel: MainActivityViewModel

    override fun addOngoingGameUiComponent(module: OnGoingGameUiModule): TestOngoingGameUiComponent
}
