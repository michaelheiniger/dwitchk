package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> Navigator(
    destination: State<T?>,
    onNavigationEvent: (T) -> Unit
) {
    val navigated = remember { mutableStateOf(false) }
    destination.value?.let { dest ->
        if (!navigated.value) {
            navigated.value = true
            onNavigationEvent(dest)
        }
    }
}