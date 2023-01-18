package ch.qscqlmpa.dwitch.app

import android.content.Context
import ch.qscqlmpa.dwitch.MainActivityComponent
import ch.qscqlmpa.dwitch.ingame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ingame.services.HostInGameService
import ch.qscqlmpa.dwitch.service.AndroidServicesModule
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.di.GameComponent
import dagger.BindsInstance
import dagger.Component
import io.reactivex.rxjava3.core.Scheduler

@AppScope
@Component(
    dependencies = [GameComponent::class],
    modules = [
        ApplicationModule::class,
        SubcomponentsModule::class,
        AndroidServicesModule::class
    ]
)
interface AppComponent {

    val appEventRepository: AppEventRepository
    val uiScheduler: Scheduler

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance idlingResource: DwitchIdlingResource,
            gameComponent: GameComponent
        ): AppComponent
    }

    fun mainActivityComponentFactory(): MainActivityComponent.Factory

    fun inject(activity: QrCodeScannerActivity)
    fun inject(service: HostInGameService)
    fun inject(service: GuestInGameService)
}
