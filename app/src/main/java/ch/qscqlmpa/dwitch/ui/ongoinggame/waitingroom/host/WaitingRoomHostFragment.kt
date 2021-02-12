package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentWaitingRoomHostBinding
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView
import mu.KLogging

class WaitingRoomHostFragment : OngoingGameBaseFragment(R.layout.fragment_waiting_room_host) {

    private lateinit var viewModel: WaitingRoomHostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWaitingRoomHostBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WaitingRoomHostViewModel::class.java)
        viewModel.canGameBeLaunched().updateView(binding.launchGameBtn, viewLifecycleOwner)
        binding.launchGameBtn.setOnClickListener { viewModel.launchGame() }
        binding.cancelGameBtn.setOnClickListener { viewModel.cancelGame() }
        setupCommands()
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupCommands() {
        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                when (command) {
                    WaitingRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
                    WaitingRoomHostCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForHost(requireActivity())
                }
            }
        )
    }

    companion object : KLogging()
}
