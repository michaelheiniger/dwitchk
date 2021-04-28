package ch.qscqlmpa.dwitchgame.computerplayer

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.*
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameInfoFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.LocalPlayerDashboard
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class Computer @Inject constructor(
    private val communicator: ComputerCommunicator,
    private val dwitchEngineFactory: DwitchEngineFactory
) {
    private val disposableManager = DisposableManager()
    private var playerCounter = 0L
    private var dwitchIdConnectionIdMap: MutableMap<DwitchPlayerId, ConnectionId> = mutableMapOf()

    fun addNewPlayer() {
        Logger.info { "Add computer player (id: ${playerCounter + 1})" }
        if (playerCounter == 0L) {
            start()
        }
        communicator.sendCommunicationEventFromComputerPlayer(ServerCommunicationEvent.ClientConnected(ConnectionId(playerCounter++)))
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                ConnectionId(playerCounter),
                GuestMessageFactory.createJoinGameMessage("Computer $playerCounter")
            )
        )
    }

    fun resumeExistingPlayer(gameCommonId: GameCommonId, dwitchPlayerId: DwitchPlayerId) {
        Logger.info { "Resume computer player (id: ${playerCounter + 1})" }
        if (playerCounter == 0L) {
            start()
        }
        communicator.sendCommunicationEventFromComputerPlayer(ServerCommunicationEvent.ClientConnected(ConnectionId(playerCounter++)))
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                ConnectionId(playerCounter),
                GuestMessageFactory.createRejoinGameMessage(gameCommonId, dwitchPlayerId)
            )
        )
    }

    private fun start() {
        communicator.observeMessagesForComputerPlayers()
            .doOnNext { envelope -> Logger.debug { "Message received by computer(s): $envelope" } }
            .subscribe(
                { envelope -> processMessage(envelope) },
                { error -> Logger.error(error) { "Error while observing messages sent to computer players." } }
            )
    }

    private fun stop() {
        disposableManager.disposeAndReset()
    }

    private fun processMessage(envelope: EnvelopeToSend) {
        when (val message = envelope.message) {
            Message.CancelGameMessage, Message.GameOverMessage -> stop()
            is Message.LaunchGameMessage -> processGameStateUpdate(message.gameState)
            is Message.GameStateUpdatedMessage -> processGameStateUpdate(message.gameState)
            is Message.JoinGameAckMessage -> {
                dwitchIdConnectionIdMap[message.playerId] = (envelope.recipient as Recipient.Single).id
                sendPlayerReadyMessage(envelope.recipient as Recipient.Single, message.playerId)
            }
            is Message.RejoinGameAckMessage -> {
                dwitchIdConnectionIdMap[message.playerId] = (envelope.recipient as Recipient.Single).id
                sendPlayerReadyMessage(envelope.recipient as Recipient.Single, message.playerId)
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun sendPlayerReadyMessage(recipient: Recipient.Single, playerId: DwitchPlayerId) {
        val messageToSend = GuestMessageFactory.createPlayerReadyMessage(playerId, ready = true)
        communicator.sendMessageToHostFromComputerPlayer(EnvelopeReceived(recipient.id, messageToSend))
    }

    private fun processGameStateUpdate(gameState: DwitchGameState) {
        val dwitchEngine = dwitchEngineFactory.create(gameState)
        val gameInfo = dwitchEngine.getGameInfo()
        when (gameInfo.gamePhase) {
            DwitchGamePhase.RoundIsBeginning,
            DwitchGamePhase.RoundIsOnGoing -> playIfNeeded(dwitchEngine, gameInfo)
            DwitchGamePhase.CardExchange -> performCardExchangeIfNeeded(dwitchEngine, gameInfo)
            else -> {
                // Nothing to do
            }
        }
    }

    private fun playIfNeeded(dwitchEngine: DwitchEngine, gameInfo: DwitchGameInfo) {
        val playingPlayerId = dwitchIdConnectionIdMap.keys.find { id -> id == gameInfo.currentPlayerId }

        if (playingPlayerId != null) {
            val dashboardInfo =
                GameInfoFactory.createGameDashboardInfo(gameInfo, playingPlayerId, PlayerConnectionState.CONNECTED)
            val playerDashboard = dashboardInfo.localPlayerDashboard

            val gameStateUpdated = when {
                playerDashboard.canPlay -> playOrPass(playerDashboard, playingPlayerId, dwitchEngine)
                playerDashboard.canPass -> passTurn(dwitchEngine, playingPlayerId)
                else -> {
                    Logger.warn("Current player is computer player but cannot play a card nor pass.")
                    null
                    // Nothing to do ???
                }
            }

            if (gameStateUpdated != null) {
                Thread.sleep(2000) // Pace the players so that the humans can see what's going on
                sendUpdatedGameState(playingPlayerId, gameStateUpdated)
            }
        }
    }

    private fun playOrPass(
        playerDashboard: LocalPlayerDashboard,
        playingPlayerId: DwitchPlayerId,
        dwitchEngine: DwitchEngine
    ): DwitchGameState {
        return when {
            dwitchEngine.isLastCardPlayedTheFirstJackOfTheRound() -> {
                // Don't want to break the "First Jack of the round" special rule, so the player passes
                passTurn(dwitchEngine, playingPlayerId)
            }
            playerHasOnlyOneNonJokerCard(playerDashboard.cardsInHand, dwitchEngine.joker()) -> {
                // Play joker to prevent breaking the "finish with joker" special rule
                playJoker(playerDashboard, dwitchEngine, playingPlayerId)
            }
            else -> playCardWithSmallestValueOrPassTurn(playerDashboard, playingPlayerId, dwitchEngine)
        }
    }

    private fun playerHasOnlyOneNonJokerCard(cardsInHand: List<DwitchCardInfo>, joker: CardName): Boolean {
        val (jokers, others) = cardsInHand.partition { c -> c.card.name == joker }
        return jokers.isNotEmpty() && others.size == 1
    }

    private fun playJoker(
        playerDashboard: LocalPlayerDashboard,
        dwitchEngine: DwitchEngine,
        playingPlayerId: DwitchPlayerId
    ): DwitchGameState {
        val cardToPlay = playerDashboard.cardsInHand.find { c -> c.card.name == dwitchEngine.joker() }!!
        Logger.debug { "Computer player with dwitch id $playingPlayerId plays card $cardToPlay." }
        return dwitchEngine.playCard(cardToPlay.card)
    }

    private fun playCardWithSmallestValueOrPassTurn(
        playerDashboard: LocalPlayerDashboard,
        playingPlayerId: DwitchPlayerId,
        dwitchEngine: DwitchEngine
    ): DwitchGameState {
        val cardsSortedAsc = playerDashboard.cardsInHand.sortedWith(DwitchCardInfoValueAscComparator())
        val cardToPlay = cardsSortedAsc.find { c -> c.selectable }?.card
        return if (cardToPlay != null) {
            Logger.debug { "Computer player with dwitch id $playingPlayerId plays card $cardToPlay." }
            dwitchEngine.playCard(cardToPlay)
        } else {
            passTurn(dwitchEngine, playingPlayerId)
        }
    }

    private fun performCardExchangeIfNeeded(dwitchEngine: DwitchEngine, gameInfo: DwitchGameInfo) {
        dwitchIdConnectionIdMap.entries.forEach { (dwitchId, connectionId) ->
            val cardExchange = dwitchEngine.getCardExchangeIfRequired(dwitchId)
            if (cardExchange != null) {
                val player = gameInfo.playerInfos.getValue(dwitchId)
                val cardsInHand = player.cardsInHand.map(DwitchCardInfo::card)

                val cardsForExchange = when (player.rank) {
                    DwitchRank.ViceAsshole, DwitchRank.Asshole -> chooseCardsWithHighestValues(cardExchange, cardsInHand)
                    DwitchRank.VicePresident, DwitchRank.President -> chooseCardsWithLowestValues(cardExchange, cardsInHand)
                    else -> throw IllegalStateException("Neutral players don't take part in card exchange.")
                }

                val gameStateUpdated = dwitchEngine.chooseCardsForExchange(dwitchId, cardsForExchange)

                sendUpdatedGameState(dwitchId, gameStateUpdated)
                communicator.sendMessageToHostFromComputerPlayer(
                    EnvelopeReceived(connectionId, MessageFactory.createGameStateUpdatedMessage(gameStateUpdated))
                )
            }
        }
    }

    private fun chooseCardsWithHighestValues(cardExchange: DwitchCardExchange, cards: List<Card>): Set<Card> {
        val remainingAllowedCardValues = cardExchange.allowedCardValues.toMutableList()
        val cardsSortedAsc = cards
            .filter { c -> remainingAllowedCardValues.remove(c.name) }
            .sortedWith(CardValueDescComparator())
        return (1..cardExchange.numCardsToChoose).map { i -> cardsSortedAsc[i - 1] }.toSet()
    }

    private fun chooseCardsWithLowestValues(cardExchange: DwitchCardExchange, cards: List<Card>): Set<Card> {
        val cardsSortedDesc = cards.sortedWith(CardValueAscComparator())
        return (1..cardExchange.numCardsToChoose).map { i -> cardsSortedDesc[i - 1] }.toSet()
    }

    private fun passTurn(dwitchEngine: DwitchEngine, playingPlayerId: DwitchPlayerId): DwitchGameState {
        Logger.debug { "Computer player with dwitch id $playingPlayerId passes its turn." }
        return dwitchEngine.passTurn()
    }

    private fun sendUpdatedGameState(playingPlayerId: DwitchPlayerId, gameStateUpdated: DwitchGameState) {
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                dwitchIdConnectionIdMap.getValue(playingPlayerId),
                MessageFactory.createGameStateUpdatedMessage(gameStateUpdated)
            )
        )
    }
}