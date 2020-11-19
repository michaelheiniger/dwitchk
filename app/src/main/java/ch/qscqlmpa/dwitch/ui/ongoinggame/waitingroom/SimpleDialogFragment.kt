package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Resource

class SimpleDialogFragment : DialogFragment() {

    private lateinit var mainTextResource: Resource

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.simple_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainTextTv(view)
        setupOkBtn(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val mainTextResourceId = arguments?.getInt(EXTRA_MAIN_TEXT_CONTENT)
            if (mainTextResourceId == null || mainTextResourceId == 0) {
                throw IllegalArgumentException("The main text of the dialog fragment must be specified.")
            }
            mainTextResource = Resource(mainTextResourceId)
        }
    }

    private fun setupMainTextTv(view: View) {
        val mainTextTv = view.findViewById<TextView>(R.id.mainTextTv)
        mainTextTv.text = getString(mainTextResource.id)
    }

    private fun setupOkBtn(view: View) {
        val okBtn = view.findViewById<Button>(R.id.btnOk)
        okBtn.setOnClickListener {
            val dialogListener = targetFragment as DialogListener
            dialogListener.onOkClicked()
            dismiss()
        }
    }

    companion object {

        private const val EXTRA_MAIN_TEXT_CONTENT = "mainTextContent"

        @JvmStatic
        fun newInstance(targetFragment: Fragment, mainTextResourceId: Int): DialogFragment {
            val dialogFragment = SimpleDialogFragment()
            val arguments = Bundle()
            arguments.putInt(EXTRA_MAIN_TEXT_CONTENT, mainTextResourceId)
            dialogFragment.arguments = arguments
            dialogFragment.setTargetFragment(targetFragment, 1)
            return dialogFragment
        }
    }

    interface DialogListener {
        fun onOkClicked()
    }
}
