package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.SimpleDialogFragment

class GameRoomGuestFragment : OngoingGameBaseFragment(R.layout.fragment_game_room_guest) {

    private lateinit var viewModel: GameRoomGuestViewModel

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        parentFragmentManager.beginTransaction()
//            .add(R.id.connection_fragment_container, ConnectionGuestFragment.create())
//            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameRoomGuestViewModel::class.java)
        observeCommands()
    }

    private fun observeCommands() {
        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                when (command) {
                    GameRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
                    GameRoomGuestCommand.ShowGameOverInfo -> showGameOverDialog()
                }
            }
        )
    }

    private fun showGameOverDialog() {
        val dialog = SimpleDialogFragment.newInstance(R.string.game_over)
        dialog.show(parentFragmentManager, "game_over_dialog")
        dialog.setFragmentResultListener(SimpleDialogFragment.requestKey) { _, _ -> viewModel.acknowledgeGameOver() }
    }

    companion object {
        fun create(): GameRoomGuestFragment {
            return GameRoomGuestFragment()
        }
    }
}
