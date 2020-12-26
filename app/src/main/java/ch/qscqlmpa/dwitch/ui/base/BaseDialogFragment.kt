package ch.qscqlmpa.dwitch.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {

    /**
     * Provide the layout resource
     *
     * @return
     */
    @get:LayoutRes
    protected abstract val layoutResource: Int

    protected abstract fun inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResource, container, false)
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }
}