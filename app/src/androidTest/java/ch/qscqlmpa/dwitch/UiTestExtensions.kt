package ch.qscqlmpa.dwitch

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.ui.common.UiTags

fun ComposeContentTestRule.assertTextIsDisplayedOnce(
    textAsId: String,
    vararg containedStrings: String
): SemanticsNodeInteraction {
    val sni = onNodeWithText(textAsId, substring = true)
    containedStrings.forEach(sni::assertTextContains)
    return sni
}

fun ComposeContentTestRule.assertCheckboxChecked(
    checkboxTag: String,
    checked: Boolean
): SemanticsNodeInteraction {
    val sni = onNodeWithTag(checkboxTag)
    if (checked) {
        sni.assertIsOn()
    } else {
        sni.assertIsOff()
    }
    return sni
}

fun ComposeContentTestRule.clickOnDialogConfirmonButton() {
    onNodeWithTag(UiTags.confirmBtn).performClick()
}
