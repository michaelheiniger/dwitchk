package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ch.qscqlmpa.dwitch.R

class EndOfRoundDialogFragment : DialogFragment() {

    private lateinit var endOfRoundInfo: List<PlayerEndOfRoundInfo>

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
            endOfRoundInfo = arguments?.getParcelableArrayList(EXTRA_DATA)!!
        }
    }

    private fun setupMainTextTv(view: View) {
        val mainTextTv = view.findViewById<TextView>(R.id.mainTextTv)
        mainTextTv.text = endOfRoundInfo
            .joinToString(separator = "\n") { info -> "${info.name}: ${getString(info.rankResource)}" }
    }

    private fun setupOkBtn(view: View) {
        val okBtn = view.findViewById<Button>(R.id.btnOk)
        okBtn.setOnClickListener { dismiss() }
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
