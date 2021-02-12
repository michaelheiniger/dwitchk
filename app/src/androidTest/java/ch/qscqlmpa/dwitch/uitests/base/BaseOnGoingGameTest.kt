package ch.qscqlmpa.dwitch.uitests.base

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil

abstract class BaseOnGoingGameTest : BaseUiTest() {

    protected fun closeEndOfRoundDialog() {
        UiUtil.clickOnButton(R.id.okBtn)
    }
}
