package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.base.BaseFragment
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import javax.inject.Inject
import javax.inject.Named

abstract class OngoingGameBaseFragment : BaseFragment() {

    @Named("ongoingGame")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
}