package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CardExchangeManagerTest {

    private val mockFacade = mockk<GameFacade>(relaxed = true)

    private lateinit var cardExchangeManager: CardExchangeManager

    private lateinit var gameDataSubject: PublishSubject<DwitchState>

    private val cardExchangeInfo = CardExchangeInfo(
        DwitchCardExchange(
            playerId = DwitchPlayerId(1),
            numCardsToChoose = 2,
            allowedCardValues = listOf(CardName.Ace, CardName.Jack)
        ),
        cardsInHand = listOf(Card.Clubs3, Card.Diamonds5, Card.ClubsAce, Card.HeartsJack)
    )

    @Before
    fun setup() {
        gameDataSubject = PublishSubject.create()
        every { mockFacade.observeGameData() } returns gameDataSubject
        cardExchangeManager = CardExchangeManager(mockFacade)
    }

    @Test
    fun `Emit screen updates when card exchange starts`() {
        // Given
        val screenObserver = cardExchangeManager.observeScreen().test()

        // When
        gameDataSubject.onNext(DwitchState.CardExchange(cardExchangeInfo))

        // Then
        screenObserver.assertValueCount(1)
        assertThat(screenObserver.values()[0]).isInstanceOf(GameRoomScreen.CardExchange::class.java)
    }

    @Test
    fun `Emit screen updates when card exchange is on going`() {
        // Given
        val screenObserver = cardExchangeManager.observeScreen().test()

        // When
        gameDataSubject.onNext(DwitchState.CardExchangeOnGoing)

        // Then
        screenObserver.assertValueCount(1)
        assertThat(screenObserver.values()[0]).isInstanceOf(GameRoomScreen.CardExchangeOnGoing::class.java)
    }

    @Test
    fun `Click on card updates the screen`() {
        // Given
        val screenObserver = cardExchangeManager.observeScreen().test()
        gameDataSubject.onNext(DwitchState.CardExchange(cardExchangeInfo))

        // When
        cardExchangeManager.onCardToExchangeClick(Card.HeartsJack)

        // Then
        screenObserver.assertValueCount(2)
    }
}