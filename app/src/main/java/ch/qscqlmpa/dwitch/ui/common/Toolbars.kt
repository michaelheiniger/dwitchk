package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ch.qscqlmpa.dwitch.R


data class NavigationIcon(
    val icon: Int,
    val contentDescription: Int,
    val onNavigationIconClick: () -> Unit = {}
)

const val toolbarDefaultTitle = "Dwitch"

@Composable
fun DwitchTopBar(
    title: Int,
    navigationIcon: NavigationIcon? = null
) {
    DwitchTopBar(title = stringResource(title), navigationIcon = navigationIcon)
}

@Composable
fun DwitchTopBar(
    title: String,
    navigationIcon: NavigationIcon?
) {
    val titleCd = stringResource(R.string.current_screen_name_cd, title)
    if (navigationIcon != null) {
        TopAppBar(
            title = { TopAppBarTitle(title, titleCd) },
            navigationIcon = {
                IconButton(
                    onClick = navigationIcon.onNavigationIconClick,
                    modifier = Modifier.testTag(UiTags.toolbarNavigationIcon)
                ) {
                    Icon(
                        painter = painterResource(navigationIcon.icon),
                        contentDescription = stringResource(navigationIcon.contentDescription)
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { TopAppBarTitle(title, titleCd) })
    }
}

@Composable
private fun TopAppBarTitle(title: String, titleCd: String) {
    Text(
        text = title,
        modifier = Modifier.semantics { this.contentDescription = titleCd }
    )
}