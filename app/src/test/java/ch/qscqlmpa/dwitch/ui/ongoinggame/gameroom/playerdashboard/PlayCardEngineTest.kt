package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PlayCardEngineTest {

    private lateinit var playCardEngine: PlayCardEngine

    private lateinit var cardsInHand: List<CardInfo>
    private var lastCardPlayed: PlayedCards? = null

    @Test
    fun `Table is empty so player can select any number of cards`() {
        lastCardPlayed = null

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Clubs3, selectable = true),
            CardInfo(Card.Diamonds3, selectable = true),
            CardInfo(Card.Hearts3, selectable = true),
            CardInfo(Card.Spades3, selectable = true),
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()

        assertThat(playCardEngine.getCardsInHand()).containsExactly(
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.Diamonds2, selectable = true, selected = false),
            CardInfo(Card.Clubs3, selectable = true, selected = false),
            CardInfo(Card.Diamonds3, selectable = true, selected = false),
            CardInfo(Card.Hearts3, selectable = true, selected = false),
            CardInfo(Card.Spades3, selectable = true, selected = false),
            CardInfo(Card.Spades9, selectable = true, selected = false),
            CardInfo(Card.Diamonds9, selectable = true, selected = false),
            CardInfo(Card.Clubs9, selectable = true, selected = false)
        )

        playCardEngine.onCardClick(Card.Clubs3) // One card selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),
            CardInfo(Card.Clubs3, selectable = true, selected = true),
            CardInfo(Card.Diamonds3, selectable = true, selected = false),
            CardInfo(Card.Hearts3, selectable = true, selected = false),
            CardInfo(Card.Spades3, selectable = true, selected = false),
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )

        playCardEngine.onCardClick(Card.Diamonds3) // Two cards selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),
            CardInfo(Card.Clubs3, selectable = true, selected = true),
            CardInfo(Card.Diamonds3, selectable = true, selected = true),
            CardInfo(Card.Hearts3, selectable = true, selected = false),
            CardInfo(Card.Spades3, selectable = true, selected = false),
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )

        playCardEngine.onCardClick(Card.Hearts3) // Three cards selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),
            CardInfo(Card.Clubs3, selectable = true, selected = true),
            CardInfo(Card.Diamonds3, selectable = true, selected = true),
            CardInfo(Card.Hearts3, selectable = true, selected = true),
            CardInfo(Card.Spades3, selectable = true, selected = false),
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )

        playCardEngine.onCardClick(Card.Spades3) // Four cards selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),
            CardInfo(Card.Clubs3, selectable = true, selected = true),
            CardInfo(Card.Diamonds3, selectable = true, selected = true),
            CardInfo(Card.Hearts3, selectable = true, selected = true),
            CardInfo(Card.Spades3, selectable = true, selected = true),
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )
    }

    @Test
    fun `Last card on table has multiplicity one so player can select exactly one card with multiplicity at least one `() {
        lastCardPlayed = PlayedCards(Card.Spades4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false),
            CardInfo(Card.Diamonds4, selectable = true),
            CardInfo(Card.Hearts4, selectable = true),
            CardInfo(Card.Clubs5, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true),
            CardInfo(Card.Spades5, selectable = true),
            CardInfo(Card.Clubs6, selectable = true),
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Joker, multiplicity 2 > 1
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.Diamonds2, selectable = true, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Value equal, multiplicity 2 > 1
            CardInfo(Card.Diamonds4, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = true, selected = false),

            // Value higher, multiplicity 4 > 1
            CardInfo(Card.Clubs5, selectable = true, selected = false),
            CardInfo(Card.Diamonds5, selectable = true, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false),

            // Value higher but multiplicity 1 == 1
            CardInfo(Card.Clubs6, selectable = true, selected = false),

            // Value higher, multiplicity 3 > 1
            CardInfo(Card.Spades9, selectable = true, selected = false),
            CardInfo(Card.Diamonds9, selectable = true, selected = false),
            CardInfo(Card.Clubs9, selectable = true, selected = false)
        )

        playCardEngine.onCardClick(Card.Clubs5) // One card selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Joker, multiplicity 2 > 1
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Value equal, multiplicity 2 > 1
            CardInfo(Card.Diamonds4, selectable = false, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),

            // Value higher, multiplicity 4 > 1
            CardInfo(Card.Clubs5, selectable = true, selected = true),
            CardInfo(Card.Diamonds5, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = false, selected = false),

            // Value higher but multiplicity 1 == 1
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Value higher, multiplicity 3 > 1
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )
    }

    @Test
    fun `Last card on table has multiplicity two so player can select exactly two cards with multiplicity at least two `() {
        lastCardPlayed = PlayedCards(Card.Spades4, Card.Clubs4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false),
            CardInfo(Card.Diamonds4, selectable = true),
            CardInfo(Card.Hearts4, selectable = true),
            CardInfo(Card.Clubs5, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true),
            CardInfo(Card.Spades5, selectable = true),
            CardInfo(Card.Clubs6, selectable = false), // Not selectable because multiplicity lower than last card played
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Joker, multiplicity 2 == 2
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.Diamonds2, selectable = true, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Value equal, multiplicity 2 == 2
            CardInfo(Card.Diamonds4, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = true, selected = false),

            // Value higher, multiplicity 4 > 2
            CardInfo(Card.Clubs5, selectable = true, selected = false),
            CardInfo(Card.Diamonds5, selectable = true, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false),

            // Value higher but multiplicity 1 < 2
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Value higher, multiplicity 3 > 2
            CardInfo(Card.Spades9, selectable = true, selected = false),
            CardInfo(Card.Diamonds9, selectable = true, selected = false),
            CardInfo(Card.Clubs9, selectable = true, selected = false)
        )

        playCardEngine.onCardClick(Card.Clubs5) // One card selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Different card name than selected card
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Diamonds4, selectable = false, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),

            // Same card name than selected card and only one card selected
            CardInfo(Card.Clubs5, selectable = true, selected = true),
            CardInfo(Card.Diamonds5, selectable = true, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false),

            // Different card name than selected card
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )

        playCardEngine.onCardClick(Card.Diamonds5) // Two cards selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Different card name than selected card
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Diamonds4, selectable = false, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),

            // Same card name than selected card and exactly two cards selected, that's the multiplicity of the last card played.
            CardInfo(Card.Clubs5, selectable = true, selected = true),
            CardInfo(Card.Diamonds5, selectable = true, selected = true),
            CardInfo(Card.Hearts5, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )

        playCardEngine.onCardClick(Card.Diamonds5) // Remove card --> one card selected
        playCardEngine.onCardClick(Card.Spades5) // add card --> two cards selected

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Different card name than selected card
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Diamonds2, selectable = false, selected = false),

            // Value too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Diamonds4, selectable = false, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),

            // Same card name than selected card and exactly two cards selected, that's the multiplicity of the last card played.
            CardInfo(Card.Clubs5, selectable = true, selected = true),
            CardInfo(Card.Diamonds5, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = true),

            // Different card name than selected card
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Different card name than selected card
            CardInfo(Card.Spades9, selectable = false, selected = false),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs9, selectable = false, selected = false)
        )
    }

    @Test
    fun `Any selected card can be unselected - table empty`() {
        lastCardPlayed = null

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Clubs3, selectable = true),
            CardInfo(Card.Diamonds3, selectable = true),
            CardInfo(Card.Hearts3, selectable = true),
            CardInfo(Card.Hearts9, selectable = true),
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()

        playCardEngine.onCardClick(Card.Clubs2) // Select
        playCardEngine.onCardClick(Card.Diamonds2) // Select
        playCardEngine.onCardClick(Card.Clubs2) // Unselect
        playCardEngine.onCardClick(Card.Diamonds2) // Unselect
    }

    @Test
    fun `Any selected card can be unselected - table not empty`() {
        lastCardPlayed = PlayedCards(Card.Spades4, Card.Clubs4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false),
            CardInfo(Card.Diamonds4, selectable = true),
            CardInfo(Card.Hearts4, selectable = true),
            CardInfo(Card.Clubs5, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true),
            CardInfo(Card.Spades5, selectable = true),
            CardInfo(Card.Clubs6, selectable = true),
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Clubs5) // Select
        playCardEngine.onCardClick(Card.Diamonds5) // Select
        playCardEngine.onCardClick(Card.Clubs5) // Unselect
        playCardEngine.onCardClick(Card.Diamonds5) // Unselect
    }

    @Test
    fun `Only cards with a multiplicity higher or equal than the multiplicity of the last card played are selectable`() {
        lastCardPlayed = PlayedCards(Card.Spades4, Card.Clubs4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false),
            CardInfo(Card.Diamonds4, selectable = true),
            CardInfo(Card.Hearts4, selectable = true),
            CardInfo(Card.Clubs5, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true),
            CardInfo(Card.Spades5, selectable = true),
            CardInfo(Card.Clubs6, selectable = true),
            CardInfo(Card.Spades9, selectable = true),
            CardInfo(Card.Diamonds9, selectable = true),
            CardInfo(Card.Clubs9, selectable = true)
        )

        setupTest()

        assertThat(playCardEngine.getCardsInHand()).containsExactly(

            // Multiplicity is 2 == 2 and joker
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.Diamonds2, selectable = true, selected = false),

            // Multiplicity is 2 == 2 but value is too low
            CardInfo(Card.Diamonds3, selectable = false, selected = false),
            CardInfo(Card.Hearts3, selectable = false, selected = false),

            // Multiplicity is 2 == 2 and value is equal
            CardInfo(Card.Diamonds4, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = true, selected = false),

            // Multiplicity is 4 > 2 and value is higher
            CardInfo(Card.Clubs5, selectable = true, selected = false),
            CardInfo(Card.Diamonds5, selectable = true, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false),

            // Multiplicity is 1 < 2
            CardInfo(Card.Clubs6, selectable = false, selected = false),

            // Multiplicity is 3 > 2 and value is higher
            CardInfo(Card.Spades9, selectable = true, selected = false),
            CardInfo(Card.Diamonds9, selectable = true, selected = false),
            CardInfo(Card.Clubs9, selectable = true, selected = false)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Player cannot select a card that is not in its hand`() {
        lastCardPlayed = null

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Clubs5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Player cannot select a card that has another card name than the already selected card(s) - table empty`() {
        lastCardPlayed = null

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = true),
            CardInfo(Card.Hearts3, selectable = true)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Diamonds3) // Select first card

        playCardEngine.onCardClick(Card.Diamonds2) // Different name than already selected card
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Player cannot select a card that is not selectable`() {
        lastCardPlayed = PlayedCards(Card.Clubs4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds3, selectable = false),
            CardInfo(Card.Hearts3, selectable = false)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Diamonds3)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Player cannot select a card that has another card name than the already selected card(s) - table not empty`() {
        lastCardPlayed = PlayedCards(Card.Clubs4, Card.Diamonds4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Diamonds5) // Select first card

        playCardEngine.onCardClick(Card.Diamonds2) // Different name than already selected card
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Player play more card than multiplicity of last card played`() {
        lastCardPlayed = PlayedCards(Card.Clubs4)

        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = true),
            CardInfo(Card.Diamonds2, selectable = true),
            CardInfo(Card.Diamonds5, selectable = true),
            CardInfo(Card.Hearts5, selectable = true)
        )

        setupTest()
        playCardEngine.onCardClick(Card.Diamonds5) // Select first card

        playCardEngine.onCardClick(Card.Diamonds2) // Too many cards ! laste card played has multiplicity one !
    }

    private fun setupTest() {
        playCardEngine = PlayCardEngine(cardsInHand, lastCardPlayed)
    }
}