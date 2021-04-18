package ch.qscqlmpa.dwitch.app

import android.app.Application
import ch.qscqlmpa.dwitch.ongoinggame.OnGoingGameUiModule
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameUiComponent
import ch.qscqlmpa.dwitch.service.ServiceManagerModule
import ch.qscqlmpa.dwitch.ui.home.HomeActivityBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import ch.qscqlmpa.dwitchcommonutil.MyIdlingResource
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
        HomeActivityBindingModule::class,
        HomeViewModelBindingModule::class,
        ServiceManagerModule::class,
        SchedulersModule::class,
        IdlingResourceModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    val idlingResource: MyIdlingResource

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun gameComponent(gameComponent: GameComponent): Builder

        fun build(): AppComponent
    }

    fun addOngoingGameUiComponent(module: OnGoingGameUiModule): OngoingGameUiComponent
}
