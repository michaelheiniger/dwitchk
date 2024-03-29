package ch.qscqlmpa.dwitch.app

import android.app.Application
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.service.AndroidServiceBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeBindingModule
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@AppScope
@Component(
    dependencies = [GameComponent::class],
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        HomeBindingModule::class,
        AndroidServiceBindingModule::class,
        SchedulersModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    val appEventRepository: AppEventRepository

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun gameComponent(gameComponent: GameComponent): Builder

        fun build(): AppComponent
    }

    fun addInGameHostUiComponent(moduleHost: InGameHostUiModule): InGameHostUiComponent
    fun addInGameGuestUiComponent(moduleHost: InGameGuestUiModule): InGameGuestUiComponent
}
