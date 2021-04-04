package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeCommand
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeViewModel
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) // Needed because of logging
class CardExchangeViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<GameDashboardFacade>(relaxed = true)

    private lateinit var viewModel: CardExchangeViewModel

    @Before
    override fun setup() {
        super.setup()
        every { mockFacade.submitCardsForExchange(any()) } returns Completable.complete()
    }

    @Test
    fun `Cards in hand and cards chosen are updated according to players actions`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        val cardsInHand = viewModel.cardsInHandItems
        val cardsChosen = viewModel.cardsToExchangeItems
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(
            Card.Clubs2,
            Card.HeartsKing,
            Card.Spades3,
            Card.Diamonds4
        )
        assertThat(cardsChosen.value!!).isEmpty()

        viewModel.addCardToExchange(Card.HeartsKing)
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.Clubs2, Card.Spades3, Card.Diamonds4)
        assertThat(cardsChosen.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.HeartsKing)

        viewModel.addCardToExchange(Card.Clubs2)
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.Spades3, Card.Diamonds4)
        assertThat(cardsChosen.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.HeartsKing, Card.Clubs2)

        viewModel.removeCardFromExchange(Card.Clubs2)
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.Clubs2, Card.Spades3, Card.Diamonds4)
        assertThat(cardsChosen.value!!.map(CardItem::card)).containsExactlyInAnyOrder(Card.HeartsKing)
    }

    @Test
    fun `Only cards with allowed values can be selected`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        val cardsInHand = viewModel.cardsInHandItems
        val cardsChosen = viewModel.cardsToExchangeItems

        // No effect
        viewModel.addCardToExchange(Card.Spades3)
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(
            Card.Clubs2,
            Card.HeartsKing,
            Card.Spades3,
            Card.Diamonds4
        )
        assertThat(cardsChosen.value!!).isEmpty()

        // No effect
        viewModel.addCardToExchange(Card.Diamonds4)
        assertThat(cardsInHand.value!!.map(CardItem::card)).containsExactlyInAnyOrder(
            Card.Clubs2,
            Card.HeartsKing,
            Card.Spades3,
            Card.Diamonds4
        )
        assertThat(cardsChosen.value!!).isEmpty()
    }

    @Test
    fun `Only cards in the hands be selected`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        try {
            viewModel.removeCardFromExchange(Card.Hearts2) // Not in the hand
            fail("Card not in the hand cannot be selected")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("is not in the chosen card")
        }
    }

    @Test
    fun `Submit control is initially disabled and enabled once the number of cards to choose is reached - 1 card to choose`() {
        launchTest(
            1, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        val exchangeControlEnabled = viewModel.exchangeControlEnabled
        assertThat(exchangeControlEnabled.value).isFalse

        viewModel.addCardToExchange(Card.HeartsKing)
        assertThat(exchangeControlEnabled.value).isTrue

        viewModel.removeCardFromExchange(Card.HeartsKing)
        assertThat(exchangeControlEnabled.value).isFalse
    }

    @Test
    fun `Submit control is initially disabled and enabled once the number of cards to choose is reached - 2 cards to choose`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        val exchangeControlEnabled = viewModel.exchangeControlEnabled
        assertThat(exchangeControlEnabled.value).isFalse

        viewModel.addCardToExchange(Card.HeartsKing)
        assertThat(exchangeControlEnabled.value).isFalse

        viewModel.addCardToExchange(Card.Clubs2)
        assertThat(exchangeControlEnabled.value).isTrue

        viewModel.removeCardFromExchange(Card.Clubs2)
        assertThat(exchangeControlEnabled.value).isFalse
    }

    @Test
    fun `Cards chosen are sent on submit`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        viewModel.addCardToExchange(Card.HeartsKing)
        viewModel.addCardToExchange(Card.Clubs2)
        viewModel.confirmExchange()

        verify { mockFacade.submitCardsForExchange(setOf(Card.HeartsKing, Card.Clubs2)) }
    }

    @Test
    fun `Command to close is sent when cards are submitted`() {
        launchTest(
            2, listOf(CardName.Two, CardName.King),
            listOf(Card.Clubs2, Card.HeartsKing, Card.Spades3, Card.Diamonds4)
        )

        val commands = viewModel.commands

        viewModel.addCardToExchange(Card.HeartsKing)
        viewModel.addCardToExchange(Card.Clubs2)
        viewModel.confirmExchange()

        assertThat(commands.value!!).isEqualTo(CardExchangeCommand.Close)
    }

    private fun launchTest(numCardsToChoose: Int, allowedCardValues: List<CardName>, cardsInHand: List<Card>) {
        mockCardExchangeInfo(numCardsToChoose, allowedCardValues, cardsInHand)
        viewModel = CardExchangeViewModel(mockFacade, Schedulers.trampoline())
    }

    private fun mockCardExchangeInfo(numCardsToChoose: Int, allowedCardValues: List<CardName>, cardsInHand: List<Card>) {
        val cardExchange = CardExchange(PlayerDwitchId(1), numCardsToChoose, allowedCardValues)
        every { mockFacade.getCardExchangeInfo() } returns Single.just(CardExchangeInfo(cardExchange, cardsInHand))
    }
}
