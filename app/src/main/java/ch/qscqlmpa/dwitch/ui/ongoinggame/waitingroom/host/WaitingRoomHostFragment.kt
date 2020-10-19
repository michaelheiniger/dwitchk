package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import kotlinx.android.synthetic.main.waiting_room_host_fragment.*

class WaitingRoomHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.waiting_room_host_fragment

    private lateinit var viewModel: WaitingRoomHostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomHostViewModel::class.java)
        viewModel.currentCommunicationState().observe(this, Observer { resource -> communicationStateTv.text = getText(resource.id) })
        viewModel.canGameBeLaunched().observe(this, Observer { canGameBeLaunched -> launchGameBtn.isEnabled = canGameBeLaunched })
        viewModel.commands().observe(this, Observer { command -> executeNavigationCommand(command) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setupLaunchGameButton(view)
        setupCancelGameButton(view)
        return view
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    private fun setupLaunchGameButton(parentView: View) {
        val launchGame = parentView.findViewById(R.id.launchGameBtn) as Button
        launchGame.setOnClickListener { viewModel.launchGame() }
    }

    private fun setupCancelGameButton(parentView: View) {
        val cancelGame = parentView.findViewById(R.id.cancelGameBtn) as Button
        cancelGame.setOnClickListener { viewModel.cancelGame() }
    }

    private fun executeNavigationCommand(command: WaitingRoomHostCommand) {
        when (command) {
            WaitingRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
            WaitingRoomHostCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForHost(activity!!)
        }
    }

    companion object {
        fun create(): WaitingRoomHostFragment {
            return WaitingRoomHostFragment()
        }
    }
}
