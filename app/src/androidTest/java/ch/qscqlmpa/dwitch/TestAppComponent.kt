package ch.qscqlmpa.dwitch

import android.content.Context
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
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.BindsInstance
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

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance idlingResource: DwitchIdlingResource,
            gameComponent: GameComponent
        ): TestAppComponent
    }

    override fun addInGameHostUiComponent(moduleHost: InGameHostUiModule): TestInGameHostUiComponent
    override fun addInGameGuestUiComponent(moduleHost: InGameGuestUiModule): TestInGameGuestUiComponent
}
