package ch.qscqlmpa.dwitch.ui.base

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes protected open val contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected abstract fun inject()

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }
}
