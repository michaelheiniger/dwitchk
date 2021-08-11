package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

internal class CardExchangePerformer(
    private val state: CardExchangePerformerState,
    private val gameUpdater: CardExchangePerformerGameUpdater
) {

    fun cardExchangeReadyToBePerformed(): Boolean {
        return when (val numberOfPlayers = state.numberOfPlayers()) {
            0, 1 -> throw IllegalStateException("There cannot be only $numberOfPlayers players in the game.")
            2, 3 -> {
                state.cardsGivenUpByPresident().size == 2 && state.cardsGivenUpByAsshole().size == 2
            }
            else -> {
                state.cardsGivenUpByPresident().size == 2 &&
                        state.cardsGivenUpByAsshole().size == 2 &&
                        state.cardsGivenUpByVicePresident().size == 1 &&
                        state.cardsGivenUpByViceAsshole().size == 1
            }
        }
    }

    fun getUpdatedGameState(): DwitchGameState {
        gameUpdater.performCardExchange(
            state.presidentId(),
            state.assholeId(),
            state.cardsGivenUpByPresident(),
            state.cardsGivenUpByAsshole()
        )

        if (state.numberOfPlayers() >= 4) {
            gameUpdater.performCardExchange(
                state.vicePresidentId(),
                state.viceAssholeId(),
                state.cardsGivenUpByVicePresident(),
                state.cardsGivenUpByViceAsshole()
            )
        }

        gameUpdater.setGamePhase(DwitchGamePhase.RoundIsBeginning)
        return gameUpdater.buildUpdatedGameState()
    }
}
