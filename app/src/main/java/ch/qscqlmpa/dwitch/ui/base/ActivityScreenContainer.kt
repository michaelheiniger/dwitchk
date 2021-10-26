package ch.qscqlmpa.dwitch.ui.base

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    DwitchTheme {
        Scaffold {
            content()
        }
    }
}
