package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.databinding.FragmentEndOfRoundDialogBinding

class EndOfRoundDialogFragment : DialogFragment(R.layout.fragment_end_of_round_dialog) {

    private lateinit var endOfRoundInfo: List<PlayerEndOfRoundInfo>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEndOfRoundDialogBinding.bind(view)
        setupMainTextTv(binding)
        setupOkBtn(binding)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            endOfRoundInfo = arguments?.getParcelableArrayList(EXTRA_DATA)!!
        }
    }

    private fun setupMainTextTv(binding: FragmentEndOfRoundDialogBinding) {
        binding.mainTextTv.text =
            endOfRoundInfo.joinToString(separator = "\n") { info -> "${info.name}: ${getString(info.rankResource)}" }
    }

    private fun setupOkBtn(binding: FragmentEndOfRoundDialogBinding) {
        binding.okBtn.setOnClickListener { dismiss() }
    }

    companion object {

        private const val EXTRA_DATA = "data"

        @JvmStatic
        fun newInstance(endOfRoundInfo: List<PlayerEndOfRoundInfo>): DialogFragment {
            val dialogFragment = EndOfRoundDialogFragment()
            val arguments = Bundle()
            arguments.putParcelableArrayList(EXTRA_DATA, ArrayList(endOfRoundInfo))
            dialogFragment.arguments = arguments
            return dialogFragment
        }
    }
}
