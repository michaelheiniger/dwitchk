package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentConnectionGuestBinding
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView

class ConnectionGuestFragment : OngoingGameBaseFragment(R.layout.fragment_connection_guest) {

    private lateinit var viewModel: ConnectionGuestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentConnectionGuestBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConnectionGuestViewModel::class.java)
        setupConnectionStateControls(binding)
        setupReconnectionControls(binding)
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupConnectionStateControls(binding: FragmentConnectionGuestBinding) {
        viewModel.connectionStateInfo().observe(viewLifecycleOwner, { uiInfo -> binding.communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupReconnectionControls(binding: FragmentConnectionGuestBinding) {
        binding.reconnectBtn.setOnClickListener { viewModel.reconnect() }
        viewModel.reconnectAction().updateView(binding.reconnectBtn, this)
        viewModel.reconnectLoading().updateView(binding.reconnectPb, this)
    }

    companion object {
        fun create(): ConnectionGuestFragment {
            return ConnectionGuestFragment()
        }
    }
}
