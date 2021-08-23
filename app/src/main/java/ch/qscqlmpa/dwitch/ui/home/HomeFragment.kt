package ch.qscqlmpa.dwitch.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.DwitchHome
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import dagger.android.support.DaggerFragment
import org.tinylog.Logger
import javax.inject.Inject
import javax.inject.Named

class HomeFragment : DaggerFragment() {

    @Named("home")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var serviceManager: ServiceManager

    @Inject
    lateinit var gameAdvertisingFacade: GameAdvertisingFacade

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DwitchHome(
                    vmFactory = viewModelFactory,
                    navigateToGameFragment = { findNavController().navigate(R.id.action_HomeFragment_to_GameFragment) }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: HomeFragmentArgs by navArgs()
        if (args.navigateFromGame) {
            serviceManager.stopGuestService()
            serviceManager.stopHostService()
        }
    }

    override fun onStart() {
        super.onStart()
        gameAdvertisingFacade.startListeningForAdvertisedGames()
    }

    override fun onStop() {
        super.onStop()
        gameAdvertisingFacade.stopListeningForAdvertisedGames()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Logger.debug { "attach HomeFragment" }
    }

    override fun onDetach() {
        super.onDetach()
        Logger.debug { "detach HomeFragment" }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
