package ch.qscqlmpa.dwitch.ui.ingame

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import org.tinylog.Logger
import javax.inject.Inject
import javax.inject.Named

class GameFragment : Fragment() {

    @Named("game")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DwitchGame(
                    vmFactory = viewModelFactory,
                    navigateToHomeFragment = { findNavController().navigate(R.id.action_GameFragment_to_HomeFragment) }
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as App).inGameUiComponent!!.inject(this)
        super.onAttach(context)
        Logger.debug { "attach GameFragment" }
    }

    override fun onDetach() {
        super.onDetach()
        Logger.debug { "detach GameFragment" }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameFragment()
    }
}
