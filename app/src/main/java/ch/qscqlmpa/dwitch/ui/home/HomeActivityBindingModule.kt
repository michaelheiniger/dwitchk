package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameActivity
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeActivityBindingModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeHostNewGameActivity(): HostNewGameActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeJoinNewGameActivity(): JoinNewGameActivity

}
