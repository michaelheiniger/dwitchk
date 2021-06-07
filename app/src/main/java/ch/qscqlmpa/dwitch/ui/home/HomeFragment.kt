package ch.qscqlmpa.dwitch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.DwitchHome
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named

class HomeFragment : DaggerFragment() {

    @Named("home")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

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

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}