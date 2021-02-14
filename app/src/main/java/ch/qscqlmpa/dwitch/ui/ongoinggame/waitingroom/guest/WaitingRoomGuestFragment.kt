package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentWaitingRoomGuestBinding
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.SimpleDialogFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateCheckbox
import mu.KLogging

class WaitingRoomGuestFragment : OngoingGameBaseFragment(R.layout.fragment_waiting_room_guest) {

    private lateinit var viewModel: WaitingRoomGuestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentWaitingRoomGuestBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WaitingRoomGuestViewModel::class.java)
        setupLocalPlayerReadyStateControls(binding)
        setupLeaveGameControls(binding)
        setupCommands()
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupLocalPlayerReadyStateControls(binding: FragmentWaitingRoomGuestBinding) {
        binding.localPlayerReadyCkb.setOnClickListener { v -> viewModel.updateReadyState((v as CheckBox).isChecked) }
        viewModel.localPlayerReadyStateInfo().updateCheckbox(binding.localPlayerReadyCkb, this)
    }

    private fun setupLeaveGameControls(binding: FragmentWaitingRoomGuestBinding) {
        binding.leaveGameBtn.setOnClickListener { viewModel.leaveGame() }
    }

    private fun setupCommands() {
        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                when (command) {
                    WaitingRoomGuestCommand.NotifyUserGameCanceled -> showGameCanceledDialog()
                    WaitingRoomGuestCommand.NavigateToHomeScreen -> MainActivity.start(requireActivity())
                    WaitingRoomGuestCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForGuest(requireActivity())
                }
            }
        )
    }

    private fun showGameCanceledDialog() {
        val dialog = SimpleDialogFragment.newInstance(R.string.game_canceled_by_host)
        dialog.show(parentFragmentManager, "game_canceled_dialog")
        dialog.setFragmentResultListener(SimpleDialogFragment.requestKey) { _, _ -> viewModel.acknowledgeGameCanceledEvent() }
    }

    companion object : KLogging() {
        fun create(): WaitingRoomGuestFragment {
            return WaitingRoomGuestFragment()
        }
    }
}
