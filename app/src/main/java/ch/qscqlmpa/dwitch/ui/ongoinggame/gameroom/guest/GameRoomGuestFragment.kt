package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.SimpleDialogFragment

class GameRoomGuestFragment : OngoingGameBaseFragment(), SimpleDialogFragment.DialogListener {

    private lateinit var viewModel: GameRoomGuestViewModel

    override val layoutResource: Int = R.layout.game_room_guest_fragment

    override fun inject() {
        (activity!!.application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.beginTransaction()
            .add(R.id.connection_fragment_container, ConnectionGuestFragment.create())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameRoomGuestViewModel::class.java)
        observeCommands()
    }

    override fun onOkClicked() {
        viewModel.acknowledgeGameOver()
    }

    private fun observeCommands() {
        viewModel.commands().observe(viewLifecycleOwner, { command ->
            when (command) {
                GameRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
                GameRoomGuestCommand.ShowGameOverInfo -> showGameOverDialog()
            }
        })
    }

    private fun showGameOverDialog() {
        showDialogFragment(SimpleDialogFragment.newInstance(this, R.string.game_over))
    }

    companion object {
        fun create(): GameRoomGuestFragment {
            return GameRoomGuestFragment()
        }
    }
}
