package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer

@Preview
@Composable
private fun YesNoDialogPreview() {
    PreviewContainer {
        YesNoDialog(R.string.host_ends_game_confirmation)
    }
}

@Composable
fun YesNoDialog(
    text: Int,
    onNoClick: () -> Unit = {},
    onYesClick: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(),
        content = {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(text), color = MaterialTheme.colors.primary)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                    }
                    TextButton(
                        onClick = { onNoClick() },
                        modifier = Modifier
                            .testTag(UiTags.dialogNoBtn)
                    ) {
                        Text(stringResource(R.string.no))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = { onYesClick() },
                        modifier = Modifier
                            .testTag(UiTags.dialogYesBtn)
                    ) {
                        Text(stringResource(R.string.yes))
                    }
                }
            }
        }
    )
}
