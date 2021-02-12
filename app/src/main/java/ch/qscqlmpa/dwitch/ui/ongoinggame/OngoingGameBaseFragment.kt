package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import ch.qscqlmpa.dwitch.ui.base.BaseFragment
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import javax.inject.Inject
import javax.inject.Named

abstract class OngoingGameBaseFragment(@LayoutRes override val contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    @Named("ongoingGame")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected fun showDialogFragment(fragment: DialogFragment, tag: String) {
//        val supportFragmentManager = requireActivity().supportFragmentManager
//        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
//        if (prev != null) ft.remove(prev)
//        ft.addToBackStack(null)

//        dialogFragment.setFragmentResult()
//        fragment.show(childFragmentManager, tag)
    }
}
