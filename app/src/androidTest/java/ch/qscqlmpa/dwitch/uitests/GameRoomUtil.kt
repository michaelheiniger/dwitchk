package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.UiUtil.assertControlEnabled
import ch.qscqlmpa.dwitch.uitests.UiUtil.clickOnButton
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
}