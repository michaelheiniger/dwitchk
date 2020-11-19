package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest


import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.SimpleDialogFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateCheckbox
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView
import kotlinx.android.synthetic.main.waiting_room_guest_fragment.*

class WaitingRoomGuestFragment : OngoingGameBaseFragment(), SimpleDialogFragment.DialogListener {

    override val layoutResource: Int = R.layout.waiting_room_guest_fragment

    private lateinit var viewModel: WaitingRoomGuestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomGuestViewModel::class.java)
        setupConnectionStateControls()
        setupLocalPlayerReadyStateControls()
        setupReconnectionControls()
        setupLeaveGameControls()
        setupCommands()
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onOkClicked() {
        viewModel.acknowledgeGameCanceledEvent()
    }

    private fun setupConnectionStateControls() {
        viewModel.connectionStateInfo().observe(this, { uiInfo -> communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupLocalPlayerReadyStateControls() {
        localPlayerReadyCkb.setOnClickListener { v -> viewModel.updateReadyState((v as CheckBox).isChecked) }
        viewModel.localPlayerReadyStateInfo().updateCheckbox(localPlayerReadyCkb, this)
    }

    private fun setupReconnectionControls() {
        reconnectBtn.setOnClickListener { viewModel.reconnect() }
        viewModel.reconnectAction().updateView(reconnectBtn, this)
        viewModel.reconnectLoading().updateView(reconnectPb, this)
    }

    private fun setupLeaveGameControls() {
        leaveGameBtn.setOnClickListener { viewModel.leaveGame() }
    }

    private fun setupCommands() {
        viewModel.commands().observe(this, { command ->
            when (command) {
                WaitingRoomGuestCommand.NotifyUserGameCanceled -> showGameCanceledDialog()
                WaitingRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
                WaitingRoomGuestCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForGuest(activity!!)
            }
        })
    }

    private fun showGameCanceledDialog() {
        showDialogFragment(SimpleDialogFragment.newInstance(this, R.string.game_canceled_by_host))
    }

    companion object {
        fun create(): WaitingRoomGuestFragment {
            return WaitingRoomGuestFragment()
        }
    }
}
