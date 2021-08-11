package ch.qscqlmpa.dwitch.ui.base

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme

@Composable
fun ActivityScreenContainer(content: @Composable () -> Unit) {
    DwitchTheme {
        Surface(color = Color.White) {
            content()
        }
    }
}
