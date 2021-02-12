package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentGameRoomHostBinding
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostFragment

class GameRoomHostFragment : OngoingGameBaseFragment(R.layout.fragment_game_room_host) {

    private lateinit var viewModel: GameRoomHostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.beginTransaction()
            .add(R.id.connection_fragment_container, ConnectionHostFragment.create())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentGameRoomHostBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameRoomHostViewModel::class.java)
        binding.endGameBtn.setOnClickListener { viewModel.endGame() }
        observeCommands()
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun observeCommands() {
        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                when (command) {
                    GameRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
                }
            }
        )
    }

    companion object {

        fun create(): GameRoomHostFragment {
            return GameRoomHostFragment()
        }
    }
}
