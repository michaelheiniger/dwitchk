package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.utils.UiUtil.updateView
import kotlinx.android.synthetic.main.connection_guest_fragment.*

class ConnectionGuestFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.connection_guest_fragment

    private lateinit var viewModel: ConnectionGuestViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ConnectionGuestViewModel::class.java)
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
        fun create(): ConnectionGuestFragment {
            return ConnectionGuestFragment()
        }
    }
}
