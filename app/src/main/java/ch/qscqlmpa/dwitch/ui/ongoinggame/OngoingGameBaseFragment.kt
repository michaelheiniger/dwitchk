package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.annotation.LayoutRes
import ch.qscqlmpa.dwitch.ui.base.BaseFragment
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import javax.inject.Inject
import javax.inject.Named

abstract class OngoingGameBaseFragment(@LayoutRes override val contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    @Named("ongoingGame")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
}
