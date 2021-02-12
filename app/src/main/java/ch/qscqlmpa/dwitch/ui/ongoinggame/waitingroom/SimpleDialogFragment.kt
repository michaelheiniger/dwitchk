package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.databinding.FragmentSimpleDialogBinding
import ch.qscqlmpa.dwitch.ui.common.Resource

class SimpleDialogFragment : DialogFragment(R.layout.fragment_simple_dialog) {

    private lateinit var mainTextResource: Resource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSimpleDialogBinding.bind(view)
        setupMainTextTv(binding)
        setupOkBtn(binding)
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

    private fun setupMainTextTv(binding: FragmentSimpleDialogBinding) {
        binding.mainTextTv.text = getString(mainTextResource.id)
    }

    private fun setupOkBtn(binding: FragmentSimpleDialogBinding) {
        binding.okBtn.setOnClickListener {
            setFragmentResult(requestKey, Bundle())
            dismiss()
        }
    }

    companion object {

        const val requestKey = "simpleDialogFragmentRequestKey"

        private const val EXTRA_MAIN_TEXT_CONTENT = "mainTextContent"

        @JvmStatic
        fun newInstance(mainTextResourceId: Int): DialogFragment {
            val dialogFragment = SimpleDialogFragment()
            val arguments = Bundle()
            arguments.putInt(EXTRA_MAIN_TEXT_CONTENT, mainTextResourceId)
            dialogFragment.arguments = arguments
            return dialogFragment
        }
    }
}
