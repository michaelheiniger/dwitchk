package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentConnectionHostBinding
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView

class ConnectionHostFragment : OngoingGameBaseFragment(R.layout.fragment_connection_host) {

    private lateinit var viewModel: ConnectionHostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentConnectionHostBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConnectionHostViewModel::class.java)
        setupConnectionStateControls(binding)
        setupReconnectionControls(binding)
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupConnectionStateControls(binding: FragmentConnectionHostBinding) {
        viewModel.connectionStateInfo().observe(viewLifecycleOwner, { uiInfo -> binding.communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupReconnectionControls(binding: FragmentConnectionHostBinding) {
        binding.reconnectBtn.setOnClickListener { viewModel.reconnect() }
        viewModel.reconnectAction().updateView(binding.reconnectBtn, this)
        viewModel.reconnectLoading().updateView(binding.reconnectPb, this)
    }

    companion object {
        fun create(): ConnectionHostFragment {
            return ConnectionHostFragment()
        }
    }
}
