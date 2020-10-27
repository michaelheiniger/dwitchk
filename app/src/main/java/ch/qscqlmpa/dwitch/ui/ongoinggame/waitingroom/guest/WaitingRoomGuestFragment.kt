package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.GameCanceledDialog
import kotlinx.android.synthetic.main.waiting_room_host_fragment.*

class WaitingRoomGuestFragment : OngoingGameBaseFragment(), GameCanceledDialog.DialogListener {

    override val layoutResource: Int = R.layout.waiting_room_guest_fragment

    private lateinit var viewModel: WaitingRoomGuestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WaitingRoomGuestViewModel::class.java)
        viewModel.currentCommunicationState().observe(this,
            { resource -> communicationStateTv.text = getText(resource.id) })
        viewModel.commands().observe(this, { command ->
            when (command) {
                WaitingRoomGuestCommand.NotifyUserGameCanceled -> showGameCanceledDialog()
                WaitingRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
                WaitingRoomGuestCommand.NotifyUserGameOver -> showGameOverPopup()
                WaitingRoomGuestCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForGuest(activity!!)
            }
        })
    }

    private fun showGameOverPopup() {
        //TODO
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setupReadyCheckbox(view)
        return view
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onDoneClicked() {
        viewModel.userAcknowledgesGameCanceledEvent()
    }

    //FIXME: State is not updated if activity is closed and re-opened.
    private fun setupReadyCheckbox(parentView: View) {
        val localPlayerReadyCkb = parentView.findViewById(R.id.localPlayerReadyCkb) as CheckBox
        localPlayerReadyCkb.setOnClickListener { v -> viewModel.updateReadyState((v as CheckBox).isChecked) }
    }

    private fun showGameCanceledDialog() {
        val supportFragmentManager = activity!!.supportFragmentManager
        val dialogFragment = GameCanceledDialog.newInstance(this)
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, "dialog")
    }

    companion object {

        fun create(): WaitingRoomGuestFragment {
            return WaitingRoomGuestFragment()
        }
    }
}
