package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import kotlinx.android.synthetic.main.game_room_host_fragment.*

class GameRoomHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.game_room_host_fragment

    private lateinit var viewModel: GameRoomHostViewModel

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameRoomHostViewModel::class.java)
        endGameBtn.setOnClickListener { viewModel.endGame() }
        observeCommands()

    }

    private fun observeCommands() {
        viewModel.commands().observe(viewLifecycleOwner, { command ->
            when (command) {
                GameRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
            }
        })
    }

    companion object {

        fun create(): GameRoomHostFragment {
            return GameRoomHostFragment()
        }
    }
}
