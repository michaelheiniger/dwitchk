package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ch.qscqlmpa.dwitch.R

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun InfoDialogPreview() {
    MaterialTheme {
        Surface(color = Color.White) {
            InfoDialog(
                title = R.string.info_dialog_title,
                text = R.string.game_canceled_by_host,
                onOkClick = {}
            )
        }
    }
}

@Composable
fun InfoDialog(
    title: Int,
    text: Int,
    onOkClick: () -> Unit
) {

    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = stringResource(title), color = Color.Black) },
            text = { Text(text = stringResource(text), color = Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onOkClick()
                        openDialog.value = false
                    },
                    modifier = Modifier.testTag(UiTags.closeInfoDialog)
                ) {
                    Text(text = stringResource(R.string.ok), color = Color.Black)
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.White
        )
    }
}

@Composable
fun ConfirmationDialog(
    title: Int,
    text: Int,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit = {}
) {

    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = onCancelClick,
            title = { Text(text = stringResource(title), color = Color.Black) },
            text = { Text(text = stringResource(text), color = Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmClick()
                        openDialog.value = false
                    },
                    modifier = Modifier.testTag(UiTags.confirmBtn)
                ) {
                    Text(text = stringResource(R.string.yes), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onCancelClick()
                        openDialog.value = false
                    },
                    modifier = Modifier.testTag(UiTags.cancelBtn)
                ) {
                    Text(text = stringResource(R.string.no), color = Color.Black)
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.White
        )
    }
}
