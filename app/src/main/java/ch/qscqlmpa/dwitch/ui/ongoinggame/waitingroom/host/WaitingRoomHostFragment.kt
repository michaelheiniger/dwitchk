package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView
import kotlinx.android.synthetic.main.waiting_room_host_fragment.*

class WaitingRoomHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.waiting_room_host_fragment

    private lateinit var viewModel: WaitingRoomHostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomHostViewModel::class.java)
        setupConnectionStateControls()
        setupCanGameBeLaunchedControls()
        setupLaunchGameButton(view)
        setupCancelGameButton(view)
        setupCommands()
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupConnectionStateControls() {
        viewModel.connectionStateInfo().observe(viewLifecycleOwner, { uiInfo -> communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupCanGameBeLaunchedControls() {
        viewModel.canGameBeLaunched().updateView(launchGameBtn, viewLifecycleOwner)
    }

    private fun setupLaunchGameButton(parentView: View) {
        val launchGame = parentView.findViewById(R.id.launchGameBtn) as Button
        launchGame.setOnClickListener { viewModel.launchGame() }
    }

    private fun setupCancelGameButton(parentView: View) {
        val cancelGame = parentView.findViewById(R.id.cancelGameBtn) as Button
        cancelGame.setOnClickListener { viewModel.cancelGame() }
    }

    private fun setupCommands() {
        viewModel.commands().observe(viewLifecycleOwner, { command ->
            when (command) {
                WaitingRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
                WaitingRoomHostCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForHost(requireActivity())
            }
        })
    }

    companion object {
        fun create(): WaitingRoomHostFragment {
            return WaitingRoomHostFragment()
        }
    }
}
