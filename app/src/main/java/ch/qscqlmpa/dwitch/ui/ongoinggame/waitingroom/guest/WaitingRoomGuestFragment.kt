package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.SimpleDialogFragment
import kotlinx.android.synthetic.main.waiting_room_host_fragment.*

class WaitingRoomGuestFragment : OngoingGameBaseFragment(), SimpleDialogFragment.DialogListener {

    override val layoutResource: Int = R.layout.waiting_room_guest_fragment

    private lateinit var viewModel: WaitingRoomGuestViewModel

    private lateinit var reconnectBtn: Button

    private lateinit var reconnectPb: ProgressBar

    private lateinit var localPlayerReadyCkb: CheckBox

    private lateinit var leaveGameBtn: Button

    //TODO: Move all the view logic in the VM: it doesn't need to know about GuestCommunicationState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomGuestViewModel::class.java)
        viewModel.currentCommunicationState().observe(this,
            { state ->
                communicationStateTv.text = getText(state.resourceId.id)
                reconnectBtn.visibility = when (state) {
                    GuestCommunicationState.Connected -> GONE
                    GuestCommunicationState.Disconnected -> VISIBLE
                    GuestCommunicationState.Error -> VISIBLE
                }

                when (state) {
                    GuestCommunicationState.Connected -> {
                        localPlayerReadyCkb.isEnabled = true
                    }
                    GuestCommunicationState.Disconnected,
                    GuestCommunicationState.Error -> {
                        localPlayerReadyCkb.isEnabled = false
                        localPlayerReadyCkb.isChecked = false
                    }

                }
                reconnectPb.visibility = GONE
                reconnectBtn.isEnabled = true
            })
        viewModel.commands().observe(this, { command ->
            when (command) {
                WaitingRoomGuestCommand.NotifyUserGameCanceled -> showGameCanceledDialog()
                WaitingRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
                WaitingRoomGuestCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForGuest(activity!!)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setupReadyCheckbox(view)
        setupReconnectButton(view)
        setupLeaveGameButton(view)
        return view
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onOkClicked() {
        viewModel.userAcknowledgesGameCanceledEvent()
    }

    //FIXME: State is not updated if activity is closed and re-opened.
    private fun setupReadyCheckbox(parentView: View) {
        localPlayerReadyCkb = parentView.findViewById(R.id.localPlayerReadyCkb) as CheckBox
        localPlayerReadyCkb.setOnClickListener { v -> viewModel.updateReadyState((v as CheckBox).isChecked) }
    }

    private fun setupReconnectButton(parentView: View) {
        reconnectBtn = parentView.findViewById(R.id.reconnectBtn) as Button
        reconnectBtn.setOnClickListener {
            viewModel.reconnect()
            reconnectPb.visibility = VISIBLE
            reconnectBtn.isEnabled = false
        }
        reconnectPb = parentView.findViewById(R.id.reconnectPb) as ProgressBar
    }

    private fun setupLeaveGameButton(parentView: View) {
        leaveGameBtn = parentView.findViewById(R.id.leaveGameBtn) as Button
        leaveGameBtn.setOnClickListener { viewModel.leaveGame() }
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
