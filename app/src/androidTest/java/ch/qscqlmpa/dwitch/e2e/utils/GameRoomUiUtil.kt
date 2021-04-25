package ch.qscqlmpa.dwitch.e2e.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchengine.model.card.Card

object GameRoomUiUtil {

    fun ComposeContentTestRule.assertGameRoomIsDisplayed() {
        onNodeWithTag(UiTags.passTurnControl).assertIsDisplayed()
    }

    fun ComposeContentTestRule.assertCardOnTable(card: Card) {
        onNodeWithTag(UiTags.lastCardPlayed).onChildren().filterToOne(hasTestTag(card.toString())).assertIsDisplayed()
    }

    fun ComposeContentTestRule.assertCardsInHand(vararg cards: Card) {
        val sni = onNodeWithTag(UiTags.hand)
        cards.forEach { card -> sni.onChildren().filterToOne(hasTestTag(card.toString())).assertIsDisplayed() }
    }

    fun ComposeContentTestRule.assertPlayerCanPassTurn() {
        onNodeWithTag(UiTags.passTurnControl)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    fun ComposeContentTestRule.assertPlayerCannotPassTurn() {
        onNodeWithTag(UiTags.passTurnControl)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    fun ComposeContentTestRule.playCard(card: Card) {
        clickOnCardInHand(card)
    }

    fun ComposeContentTestRule.passTurn() {
        onNodeWithTag(UiTags.passTurnControl).performClick()
    }

    fun ComposeContentTestRule.chooseCardForExchange(card: Card) {
        clickOnCardInHand(card)
    }

    fun ComposeContentTestRule.assertCardExchangeControlIsDisabled() {
        onNodeWithTag(UiTags.confirmCardExchange).assertIsDisplayed()
    }

    fun ComposeContentTestRule.assertCardExchangeControlIsEnabled() {
        onNodeWithTag(UiTags.confirmCardExchange).assertIsEnabled()
    }

    fun ComposeContentTestRule.confirmCardExchange() {
        onNodeWithTag(UiTags.confirmCardExchange).performClick()
    }

    fun ComposeContentTestRule.assertCardExchangeIsOnGoing() {
        onNodeWithTag(UiTags.cardExchangeOngoing).assertIsDisplayed()
    }

    fun ComposeContentTestRule.assertEndOfRoundResult(playerName: String, rank: String) {
        onNodeWithTag(playerName).assertTextContains(rank)
    }

    fun ComposeContentTestRule.closeGameOverDialog() {
        onNodeWithTag(UiTags.closeInfoDialog).performClick()
    }

    fun ComposeContentTestRule.endGame() {
        onNodeWithTag(UiTags.endGame).performClick()
    }

    fun ComposeContentTestRule.startNewRound() {
        onNodeWithTag(UiTags.startNewRound).performClick()
    }

    private fun ComposeContentTestRule.clickOnCardInHand(card: Card) {
        onNodeWithTag(UiTags.hand)
            .onChildren()
            .filterToOne(hasTestTag(card.toString()))
            .performClick()
    }
}
