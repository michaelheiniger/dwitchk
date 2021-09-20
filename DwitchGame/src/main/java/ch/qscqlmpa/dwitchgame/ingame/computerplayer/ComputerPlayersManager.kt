package ch.qscqlmpa.dwitchgame.ingame.computerplayer

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@OngoingGameScope
internal class ComputerPlayersManager @Inject constructor(
    private val communicator: ComputerCommunicator,
    private val dwitchFactory: DwitchFactory
) {
    private val disposableManager = DisposableManager()
    private val availableConnectionIds = initializeConnectionIdPool() // Sorted ASC so that IDs are given like: 1, 2, 3, ...

    private var playerCounter: Int = 0
    private var dwitchIdConnectionIdMap: MutableMap<DwitchPlayerId, ConnectionId> = mutableMapOf()

    fun addNewPlayer() {
        val connectionId = availableConnectionIds.removeFirstOrNull()
        if (connectionId == null) {
            Logger.error("Can't add a new computer player: maximum number is reached.")
            return
        }
        Logger.info { "Add computer player (connection id: $connectionId)" }

        playerCounter++
        if (playerCounter == 1) startObservingMessages()

        connectPlayerToHost(connectionId)
        playerJoinsGame(connectionId)
    }

    fun resumeExistingPlayer(gameCommonId: GameCommonId, playerId: DwitchPlayerId) {
        val connectionId = availableConnectionIds.removeFirst()
        Logger.info { "Resume computer player (dwitch id: $playerId, connection id: $connectionId)" }

        playerCounter++
        if (playerCounter == 1) startObservingMessages()

        connectPlayerToHost(connectionId)
        playerRejoinsGame(connectionId, gameCommonId, playerId)
    }

    private fun initializeConnectionIdPool() =
        (ConnectionStore.computerConnectionIdRange).toList().map { v -> ConnectionId(v) }.toMutableList()

    private fun startObservingMessages() {
        disposableManager.add(
            communicator.observeMessagesForComputerPlayers()
                .doOnNext { envelope -> Logger.debug { "Message received by computer(s): $envelope" } }
                .subscribe(
                    { envelope -> processMessage(envelope) },
                    { error -> Logger.error(error) { "Error while observing messages sent to computer players." } }
                )
        )
    }

    private fun stopObservingMessages() {
        disposableManager.disposeAndReset()
    }

    private fun connectPlayerToHost(connectionId: ConnectionId) {
        communicator.sendCommunicationEventFromComputerPlayer(ServerEvent.CommunicationEvent.ClientConnected(connectionId))
    }

    private fun playerJoinsGame(connectionId: ConnectionId) {
        communicator.sendMessageToHostFromComputerPlayer(
            ServerEvent.EnvelopeReceived(
                connectionId,
                GuestMessageFactory.createJoinGameMessage(playerName = "Computer ${connectionId.value}", computerManaged = true)
            )
        )
    }

    private fun playerRejoinsGame(connectionId: ConnectionId, gameCommonId: GameCommonId, playerId: DwitchPlayerId) {
        communicator.sendMessageToHostFromComputerPlayer(
            ServerEvent.EnvelopeReceived(connectionId, GuestMessageFactory.createRejoinGameMessage(gameCommonId, playerId))
        )
    }

    private fun processMessage(envelope: EnvelopeToSend) {
        when (val message = envelope.message) {
            Message.CancelGameMessage, Message.GameOverMessage -> stopObservingMessages()
            is Message.KickPlayerMessage -> processKickMessage(message.playerId)
            is Message.LaunchGameMessage -> handleComputerPlayerAction(message.gameState)
            is Message.GameStateUpdatedMessage -> handleComputerPlayerAction(message.gameState)
            is Message.JoinGameAckMessage -> playerNotifiesHostThatItsReady(message.playerId, envelope)
            is Message.RejoinGameAckMessage -> playerNotifiesHostThatItsReady(message.playerId, envelope)
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleComputerPlayerAction(gameState: DwitchGameState) {
        computerPlayerEngine(gameState).handleComputerPlayerAction()
            .forEach { (dwitchId, updatedGameState) -> sendUpdatedGameState(dwitchId, updatedGameState) }
    }

    private fun playerNotifiesHostThatItsReady(playerId: DwitchPlayerId, envelope: EnvelopeToSend) {
        dwitchIdConnectionIdMap[playerId] = (envelope.recipient as Recipient.Single).id
        sendPlayerReadyMessage(envelope.recipient as Recipient.Single, playerId)
    }

    private fun computerPlayerEngine(gameState: DwitchGameState): DwitchComputerPlayerEngine {
        return dwitchFactory.createComputerPlayerEngine(gameState, dwitchIdConnectionIdMap.keys.toSet())
    }

    private fun sendPlayerReadyMessage(recipient: Recipient.Single, playerId: DwitchPlayerId) {
        val messageToSend = GuestMessageFactory.createPlayerReadyMessage(playerId, ready = true)
        communicator.sendMessageToHostFromComputerPlayer(ServerEvent.EnvelopeReceived(recipient.id, messageToSend))
    }

    private fun sendUpdatedGameState(playerId: DwitchPlayerId, gameStateUpdated: DwitchGameState) {
        communicator.sendMessageToHostFromComputerPlayer(
            ServerEvent.EnvelopeReceived(
                dwitchIdConnectionIdMap.getValue(playerId),
                MessageFactory.createGameStateUpdatedMessage(gameStateUpdated)
            )
        )
    }

    private fun processKickMessage(playerId: DwitchPlayerId) {
        val connectionIdToRecycle = dwitchIdConnectionIdMap.remove(playerId)
        if (connectionIdToRecycle != null) {
            availableConnectionIds.add(connectionIdToRecycle)
            availableConnectionIds.sortBy { id -> id.value } // So that next ID assigned is the smallest one available
        }

        playerCounter--
        if (playerCounter == 0) { // No more computer players
            stopObservingMessages()
        }
    }
}
