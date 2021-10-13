package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppComponent
import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.app.ApplicationModule
import ch.qscqlmpa.dwitch.app.SchedulersModule
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.TestInGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.TestInGameHostUiComponent
import ch.qscqlmpa.dwitch.service.AndroidServicesModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.Component

@AppScope
@Component(
    dependencies = [GameComponent::class],
    modules = [
        ApplicationModule::class,
        HomeViewModelBindingModule::class,
        AndroidServicesModule::class,
        SchedulersModule::class
    ]
)
interface TestAppComponent : AppComponent {
    override fun addInGameHostUiComponent(moduleHost: InGameHostUiModule): TestInGameHostUiComponent
    override fun addInGameGuestUiComponent(moduleHost: InGameGuestUiModule): TestInGameGuestUiComponent
}
