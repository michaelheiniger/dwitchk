package ch.qscqlmpa.dwitch

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText

fun ComposeContentTestRule.assertTextIsDisplayedOnce(
    textAsId: String,
    vararg containedStrings: String
): SemanticsNodeInteraction {
    val sni = onNodeWithText(textAsId, substring = true)
    containedStrings.forEach(sni::assertTextContains)
    return sni
}