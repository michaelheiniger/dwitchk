package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeActivityBindingModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivity(): MainActivity
}
