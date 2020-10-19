package ch.qscqlmpa.dwitch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject
constructor(private val viewModels: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider
.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return Objects.requireNonNull<Provider<ViewModel>>(viewModels[modelClass]).get() as T
        } catch (e: Exception) {
            throw RuntimeException("Error creating viewmodel for class: " + modelClass.simpleName, e)
        }
    }
}
