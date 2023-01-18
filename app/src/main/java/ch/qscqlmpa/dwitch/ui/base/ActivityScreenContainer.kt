package ch.qscqlmpa.dwitch.ui.base

import androidx.compose.runtime.Composable
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    DwitchTheme {
        content()
    }
}
