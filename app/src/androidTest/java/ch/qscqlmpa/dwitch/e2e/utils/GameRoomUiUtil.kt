package ch.qscqlmpa.dwitch.e2e.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchengine.model.card.Card

object GameRoomUiUtil {

    fun ComposeContentTestRule.assertGameRoomIsDisplayed() {
        onNodeWithTag(UiTags.passTurnControl).assertIsDisplayed()
    }

    fun ComposeContentTestRule.assertCardOnTable(vararg card: Card) {
        card.forEach { c ->
            onNodeWithTag(UiTags.lastCardPlayed, useUnmergedTree = true).onChildren().filterToOne(hasTestTag(c.toString()))
                .assertIsDisplayed()
        }
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

    fun ComposeContentTestRule.playCards(vararg card: Card) {
        card.forEach { c -> clickOnCardInHand(c) }
        clickOnPlay()
    }

    fun ComposeContentTestRule.passTurn() {
        onNodeWithTag(UiTags.passTurnControl).performClick()
    }

    fun ComposeContentTestRule.chooseCardsForExchange(vararg card: Card) {
        card.forEach { c -> clickOnCardInHand(c) }
    }

    fun ComposeContentTestRule.assertCardExchangeControlIsHidden() {
        onNodeWithTag(UiTags.confirmCardExchange).assertDoesNotExist()
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
        onNodeWithTag(UiTags.confirmBtn).performClick()
    }

    fun ComposeContentTestRule.endGame() {
        onNodeWithTag(UiTags.toolbarNavigationIcon).performClick()
    }

    fun ComposeContentTestRule.startNewRound() {
        onNodeWithTag(UiTags.startNewRound).performClick()
    }

    private fun ComposeContentTestRule.clickOnPlay() {
        onNodeWithTag(UiTags.playCardControl).performClick()
    }

    private fun ComposeContentTestRule.clickOnCardInHand(card: Card) {
        onNodeWithTag(UiTags.hand)
            .onChildren()
            .filterToOne(hasTestTag(card.toString()))
            .performClick()
    }
}
