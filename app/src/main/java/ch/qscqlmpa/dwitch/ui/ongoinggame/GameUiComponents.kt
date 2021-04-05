package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.compose.runtime.Composable
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.InfoDialog

@Composable
fun GameOverDialog(onGameOverAcknowledge: () -> Unit) {
    InfoDialog(
        title = R.string.info_dialog_title,
        text = R.string.game_canceled_by_host,
        onOkClick = onGameOverAcknowledge
    )
}