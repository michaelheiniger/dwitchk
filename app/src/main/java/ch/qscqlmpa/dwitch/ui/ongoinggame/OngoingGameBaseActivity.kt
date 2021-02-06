package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.base.BaseActivity
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import javax.inject.Inject
import javax.inject.Named

abstract class OngoingGameBaseActivity : BaseActivity() {

    @Named("ongoingGame")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
}
