package ch.qscqlmpa.dwitch.ui.home

import android.os.Bundle
import ch.qscqlmpa.dwitch.ui.BaseActivity
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import javax.inject.Named

abstract class HomeBaseActivity : BaseActivity(), HasAndroidInjector {

    @Named("home")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun androidInjector(): AndroidInjector<Any>? {
        return androidInjector
    }
}

