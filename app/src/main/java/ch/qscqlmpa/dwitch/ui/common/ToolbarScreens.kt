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
    title: String = toolbarDefaultTitle,
    navigationIcon: NavigationIcon?
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (navigationIcon != null) {
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
        }
    )
}