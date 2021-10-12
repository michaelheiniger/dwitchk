package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.HomeActivity
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeBindingModule {

    @ContributesAndroidInjector(modules = [HomeViewModelBindingModule::class])
    abstract fun contributeHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun contributeQrCodeScannerActivity(): QrCodeScannerActivity
}
