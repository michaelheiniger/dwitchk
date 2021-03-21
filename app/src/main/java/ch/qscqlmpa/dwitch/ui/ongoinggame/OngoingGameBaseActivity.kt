package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.base.BaseActivity
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import javax.inject.Inject
import javax.inject.Named

abstract class OngoingGameBaseActivity<T : BaseViewModel> : BaseActivity<T>() {

    @Named("ongoingGame")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
}
