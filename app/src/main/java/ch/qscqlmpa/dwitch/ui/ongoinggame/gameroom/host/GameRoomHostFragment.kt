package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment

class GameRoomHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.game_room_host_fragment

    private lateinit var viewModel: GameRoomHostViewModel

    private lateinit var endGameBtn: Button

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameRoomHostViewModel::class.java)
        observeCommands()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        endGameBtn = view.findViewById(R.id.endGameBtn) as Button
        endGameBtn.setOnClickListener { viewModel.endGame() }
        return view
    }

    private fun observeCommands() {
        viewModel.commands().observe(this, { command ->
            when (command) {
                GameRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
            }
        })
    }

    companion object {

        fun create(): GameRoomHostFragment {
            return GameRoomHostFragment()
        }
    }
}
