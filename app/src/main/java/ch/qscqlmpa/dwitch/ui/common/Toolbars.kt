package ch.qscqlmpa.dwitch.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.tooling.preview.Preview
import ch.qscqlmpa.dwitch.R

data class NavigationIcon(
    val icon: Int,
    val contentDescription: Int,
    val onClick: () -> Unit = {}
)

const val TOOLBAR_DEFAULT_TITLE = "Dwitch"

sealed class MenuAction(@DrawableRes val icon: Int, @StringRes val contentDescription: Int, val tag: String)

private object TestMenuAction1 : MenuAction(R.drawable.ic_outline_info_24, R.string.game_rules_info_content, "testmenuaction1")
private object TestMenuAction2 : MenuAction(R.mipmap.clubs_2, R.string.game_rules_info_content, "testmenuaction2")

object GameRules : MenuAction(
    icon = R.drawable.ic_outline_info_24,
    contentDescription = R.string.game_rules_info_content,
    tag = UiTags.gameRulesInfo
)

@Preview
@Composable
fun Preview() {
    DwitchTopBar(
        title = "Dwiiitch",
        navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.leave_game, onClick = {}),
        actions = listOf(TestMenuAction1, TestMenuAction2),
        onActionClick = {}
    )
}

@Composable
fun DwitchTopBar(@StringRes title: Int) {
    DwitchTopBar(title = stringResource(title), navigationIcon = null, emptyList(), onActionClick = {})
}

@Composable
fun <T : MenuAction> DwitchTopBar(
    @StringRes title: Int,
    navigationIcon: NavigationIcon?,
    actions: List<T> = emptyList(),
    onActionClick: (T) -> Unit
) {
    DwitchTopBar(
        title = stringResource(title),
        navigationIcon = navigationIcon,
        actions = actions,
        onActionClick = onActionClick
    )
}

@Composable
fun <T : MenuAction> DwitchTopBar(
    title: String,
    navigationIcon: NavigationIcon?,
    actions: List<T> = emptyList(),
    onActionClick: (T) -> Unit
) {
    val titleCd = stringResource(R.string.current_screen_name_cd, title)
    if (navigationIcon != null) {
        TopAppBar(
            title = { TopAppBarTitle(title, titleCd) },
            navigationIcon = {
                IconButton(
                    onClick = navigationIcon.onClick,
                    modifier = Modifier.testTag(UiTags.toolbarNavigationIcon)
                ) {
                    Icon(
                        painter = painterResource(navigationIcon.icon),
                        contentDescription = stringResource(navigationIcon.contentDescription)
                    )
                }
            },
            actions = {
                actions.forEach { action ->
                    IconButton(
                        onClick = { onActionClick(action) },
                        modifier = Modifier.testTag(action.tag)
                    ) {
                        Icon(
                            painter = painterResource(action.icon),
                            contentDescription = stringResource(action.contentDescription)
                        )
                    }
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
