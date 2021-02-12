package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView
import kotlinx.android.synthetic.main.connection_host_fragment.*

class ConnectionHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.connection_host_fragment

    private lateinit var viewModel: ConnectionHostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConnectionHostViewModel::class.java)
        setupConnectionStateControls()
        setupReconnectionControls()
    }

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    private fun setupConnectionStateControls() {
        viewModel.connectionStateInfo().observe(viewLifecycleOwner, { uiInfo -> communicationStateTv.text = getText(uiInfo.textResource.id) })
    }

    private fun setupReconnectionControls() {
        reconnectBtn.setOnClickListener { viewModel.reconnect() }
        viewModel.reconnectAction().updateView(reconnectBtn, this)
        viewModel.reconnectLoading().updateView(reconnectPb, this)
    }

    companion object {
        fun create(): ConnectionHostFragment {
            return ConnectionHostFragment()
        }
    }
}
