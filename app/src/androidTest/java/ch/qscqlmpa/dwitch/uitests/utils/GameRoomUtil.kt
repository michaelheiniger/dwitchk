package ch.qscqlmpa.dwitch.uitests.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.assertControlEnabled
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import ch.qscqlmpa.dwitchengine.model.card.Card

object GameRoomUtil {

    fun pickACard() {
        clickOnButton(R.id.pickBtn)
    }

    fun passTurn() {
        clickOnButton(R.id.passBtn)
    }

    fun assertCanPickACard(canPickACard: Boolean) {
        assertControlEnabled(R.id.pickBtn, canPickACard)
    }

    fun assertCanPassTurn(canPassTurn: Boolean) {
        assertControlEnabled(R.id.passBtn, canPassTurn)
    }

    fun assertCardInHand(position: Int, card: Card) {
        onView(withRecyclerView(R.id.cardsInHandRw).atPositionOnView(position, R.id.cardIv))
            .check(matches(withContentDescription(card.toString())))
    }

    fun assertCardOnTable(card: Card) {
        onView(withId(R.id.lastCardIv)).check(matches(withContentDescription(card.toString())))
    }

    fun assertGameRoomIsDisplayed() {
        onView(withId(R.id.pickBtn)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.passBtn)).check(matches(ViewMatchers.isDisplayed()))
    }
}