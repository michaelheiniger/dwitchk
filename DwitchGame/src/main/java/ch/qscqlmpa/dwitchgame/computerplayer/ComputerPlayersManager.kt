package ch.qscqlmpa.dwitchgame.computerplayer

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class ComputerPlayersManager @Inject constructor(
    private val communicator: ComputerCommunicator,
    private val dwitchFactory: DwitchFactory
) {
    private val disposableManager = DisposableManager()
    private var playerCounter = 0L
    private var dwitchIdConnectionIdMap: MutableMap<DwitchPlayerId, ConnectionId> = mutableMapOf()

    fun addNewPlayer() {
        playerCounter++
        Logger.info { "Add computer player (id: $playerCounter)" }
        if (playerCounter == 1L) {
            start()
        }
        if (playerCounter == maxNumOfComputerPlayers) {
            Logger.error("Maximum number of computer player is $maxNumOfComputerPlayers.")
            return
        }
        communicator.sendCommunicationEventFromComputerPlayer(ServerCommunicationEvent.ClientConnected(ConnectionId(playerCounter)))
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                ConnectionId(playerCounter),
                GuestMessageFactory.createJoinGameMessage(playerName = "Computer $playerCounter", computerManaged = true)
            )
        )
    }

    fun resumeExistingPlayer(gameCommonId: GameCommonId, dwitchPlayerId: DwitchPlayerId) {
        playerCounter++
        Logger.info { "Resume computer player (id: $playerCounter)" }
        if (playerCounter == 1L) {
            start()
        }
        communicator.sendCommunicationEventFromComputerPlayer(ServerCommunicationEvent.ClientConnected(ConnectionId(playerCounter)))
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                ConnectionId(playerCounter),
                GuestMessageFactory.createRejoinGameMessage(gameCommonId, dwitchPlayerId)
            )
        )
    }

    private fun start() {
        disposableManager.add(
            communicator.observeMessagesForComputerPlayers()
                .doOnNext { envelope -> Logger.debug { "Message received by computer(s): $envelope" } }
                .subscribe(
                    { envelope -> processMessage(envelope) },
                    { error -> Logger.error(error) { "Error while observing messages sent to computer players." } }
                )
        )
    }

    private fun stop() {
        disposableManager.disposeAndReset()
    }

    private fun processMessage(envelope: EnvelopeToSend) {
        when (val message = envelope.message) {
            Message.CancelGameMessage, Message.GameOverMessage -> stop()
            is Message.LaunchGameMessage -> processLaunchGameMessage(message)
            is Message.GameStateUpdatedMessage -> processGameStateUpdatedMessage(message)
            is Message.JoinGameAckMessage -> processJoinGameAckMessage(message, envelope)
            is Message.RejoinGameAckMessage -> processRejoinGameAckMessage(message, envelope)
            else -> {
                // Nothing to do
            }
        }
    }

    private fun processRejoinGameAckMessage(message: Message.RejoinGameAckMessage, envelope: EnvelopeToSend) {
        dwitchIdConnectionIdMap[message.playerId] = (envelope.recipient as Recipient.Single).id
        sendPlayerReadyMessage(envelope.recipient as Recipient.Single, message.playerId)
    }

    private fun processJoinGameAckMessage(message: Message.JoinGameAckMessage, envelope: EnvelopeToSend) {
        dwitchIdConnectionIdMap[message.playerId] = (envelope.recipient as Recipient.Single).id
        sendPlayerReadyMessage(envelope.recipient as Recipient.Single, message.playerId)
    }

    private fun processGameStateUpdatedMessage(message: Message.GameStateUpdatedMessage) {
        handleComputerPlayerAction(message.gameState)
    }

    private fun processLaunchGameMessage(message: Message.LaunchGameMessage) {
        handleComputerPlayerAction(message.gameState)
    }

    private fun handleComputerPlayerAction(gameState: DwitchGameState) {
        computerPlayerEngine(gameState).handleComputerPlayerAction()
            .forEach { (dwitchId, updatedGameState) -> sendUpdatedGameState(dwitchId, updatedGameState) }
    }

    private fun computerPlayerEngine(gameState: DwitchGameState): DwitchComputerPlayerEngine {
        return dwitchFactory.createComputerPlayerEngine(gameState, dwitchIdConnectionIdMap.keys.toSet())
    }

    private fun sendPlayerReadyMessage(recipient: Recipient.Single, playerId: DwitchPlayerId) {
        val messageToSend = GuestMessageFactory.createPlayerReadyMessage(playerId, ready = true)
        communicator.sendMessageToHostFromComputerPlayer(EnvelopeReceived(recipient.id, messageToSend))
    }

    private fun sendUpdatedGameState(playingPlayerId: DwitchPlayerId, gameStateUpdated: DwitchGameState) {
        communicator.sendMessageToHostFromComputerPlayer(
            EnvelopeReceived(
                dwitchIdConnectionIdMap.getValue(playingPlayerId),
                MessageFactory.createGameStateUpdatedMessage(gameStateUpdated)
            )
        )
    }

    companion object {
        const val maxNumOfComputerPlayers = 10L
    }
}