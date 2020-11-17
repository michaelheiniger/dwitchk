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
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.mapToAndroidVisibility
import kotlinx.android.synthetic.main.waiting_room_guest_fragment.*

class WaitingRoomGuestFragment : OngoingGameBaseFragment(), SimpleDialogFragment.DialogListener {

    override val layoutResource: Int = R.layout.waiting_room_guest_fragment

    private lateinit var viewModel: WaitingRoomGuestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomGuestViewModel::class.java)
        setupLocalPlayerReadyStateControls()
        setupReconnectionControls()
        setupConnectionStateControls()
        setupCommands()
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

    private fun setupConnectionStateControls() {
        viewModel.connectionStateInfo().observe(this, { uiInfo -> communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupReconnectionControls() {
        viewModel.reconnectAction().observe(this, { control ->
            reconnectBtn.visibility = mapToAndroidVisibility(control.visibility)
            reconnectBtn.isEnabled = control.enabled
        })

        viewModel.reconnectLoading().observe(this, { control ->
            reconnectPb.visibility = mapToAndroidVisibility(control.visibility)
            reconnectPb.isEnabled = control.enabled
        })
    }

    private fun setupLocalPlayerReadyStateControls() {
        viewModel.localPlayerReadyStateInfo().observe(this, { checkbox ->
            localPlayerReadyCkb.isEnabled = checkbox.enabled
            localPlayerReadyCkb.isChecked = checkbox.checked
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localPlayerReadyCkb.setOnClickListener { v -> viewModel.updateReadyState((v as CheckBox).isChecked) }
        reconnectBtn.setOnClickListener { viewModel.reconnect() }
        leaveGameBtn.setOnClickListener { viewModel.leaveGame() }
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onOkClicked() {
        viewModel.acknowledgeGameCanceledEvent()
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
