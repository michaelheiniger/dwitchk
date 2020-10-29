package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Single
import javax.inject.Inject

class HostMessageFactory @Inject constructor(private val store: InGameStore) {

    fun createWaitingRoomStateUpdateMessage(): Single<EnvelopeToSend> {
        return Single.fromCallable {
            val players = store.getPlayersInWaitingRoom()
            return@fromCallable createWaitingRoomStateUpdateMessage(players)
        }
    }

    fun createJoinAckMessage(recipientId: LocalConnectionId, playerInGameId: PlayerInGameId): Single<EnvelopeToSend> {
        return Single.fromCallable {
            val gameCommonId = store.getGame().gameCommonId
            val message = Message.JoinGameAckMessage(gameCommonId, playerInGameId)
            EnvelopeToSend(RecipientType.Single(recipientId), message)
        }
    }

    companion object {

        fun createCancelGameMessage(): EnvelopeToSend {
            return EnvelopeToSend(RecipientType.All, Message.CancelGameMessage)
        }

        fun createLaunchGameMessage(gameState: GameState): EnvelopeToSend {
            return EnvelopeToSend(RecipientType.All, Message.LaunchGameMessage(gameState))
        }

        private fun createWaitingRoomStateUpdateMessage(playerList: List<Player>): EnvelopeToSend {
            val message = Message.WaitingRoomStateUpdateMessage(playerList)
            return EnvelopeToSend(RecipientType.All, message)
        }
    }
}