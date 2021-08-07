package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppComponent
import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.app.ApplicationModule
import ch.qscqlmpa.dwitch.app.SchedulersModule
import ch.qscqlmpa.dwitch.ingame.InGameUiModule
import ch.qscqlmpa.dwitch.ingame.TestInGameUiComponent
import ch.qscqlmpa.dwitch.service.AndroidServiceBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.Component
import dagger.android.AndroidInjectionModule

@AppScope
@Component(
    dependencies = [GameComponent::class],
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        HomeBindingModule::class,
        HomeViewModelBindingModule::class,
        AndroidServiceBindingModule::class,
        SchedulersModule::class
    ]
)
interface TestAppComponent : AppComponent {

    override fun addInGameUiComponent(module: InGameUiModule): TestInGameUiComponent
}
