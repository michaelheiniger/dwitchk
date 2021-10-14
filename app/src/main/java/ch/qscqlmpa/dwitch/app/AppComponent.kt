package ch.qscqlmpa.dwitch.app

import android.content.Context
import ch.qscqlmpa.dwitch.MainActivity
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ingame.services.HostInGameService
import ch.qscqlmpa.dwitch.service.AndroidServicesModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
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
interface AppComponent {

    val appEventRepository: AppEventRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance idlingResource: DwitchIdlingResource,
            gameComponent: GameComponent
        ): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(activity: QrCodeScannerActivity)
    fun inject(service: HostInGameService)
    fun inject(service: GuestInGameService)

    fun addInGameHostUiComponent(moduleHost: InGameHostUiModule): InGameHostUiComponent
    fun addInGameGuestUiComponent(moduleHost: InGameGuestUiModule): InGameGuestUiComponent
}
