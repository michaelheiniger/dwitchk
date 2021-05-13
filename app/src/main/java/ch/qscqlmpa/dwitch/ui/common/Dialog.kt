package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
                    modifier = Modifier.testTag(UiTags.confirmBtn)
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun LoadingDialogPreview() {
    LoadingDialog()
}

@Composable
fun LoadingDialog() {
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
                    Text(stringResource(id = R.string.loading))
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    )
}
