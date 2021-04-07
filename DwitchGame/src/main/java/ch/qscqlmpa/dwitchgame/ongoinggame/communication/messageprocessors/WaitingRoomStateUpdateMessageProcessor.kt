package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomStateUpdateMessageProcessor @Inject constructor(
    private val store: InGameStore
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.WaitingRoomStateUpdateMessage

        return Completable.fromCallable {

            val playersInWr = store.getPlayersInWaitingRoom()

            removePlayersWhoLeftTheGame(msg.playerList, playersInWr)
            updatePlayersWhoseStateChanged(msg.playerList, playersInWr)
            addNewPlayers(msg.playerList, playersInWr)
        }
    }

    private fun removePlayersWhoLeftTheGame(playersUpToDate: List<PlayerWr>, playersOld: List<Player>) {
        val playersToRemove = playersOld.map(Player::dwitchId).toHashSet()
        playersToRemove.removeAll(playersUpToDate.map(PlayerWr::dwitchId))
        if (playersToRemove.size > 0) {
            Logger.trace { "Players to remove: $playersToRemove" }
            store.deletePlayers(getLocalIdOfPlayersToRemove(playersOld, playersToRemove))
        } else {
            Logger.trace { "No player to remove." }
        }
    }

    private fun getLocalIdOfPlayersToRemove(playersOld: List<Player>, playersToRemove: Set<DwitchPlayerId>): List<Long> {
        return playersOld.filter { p -> playersToRemove.contains(p.dwitchId) }.map { p -> p.id }
    }

    private fun updatePlayersWhoseStateChanged(upToDatePlayers: List<PlayerWr>, playersOld: List<Player>) {
        val upToDateOldPlayerPairs = upToDatePlayers.map { upToDatePlayer ->
            val playerOld = playersOld.find { playerOld -> playerOld.dwitchId == upToDatePlayer.dwitchId }
            Pair(upToDatePlayer, playerOld)
        }

        val playersToUpdate = upToDateOldPlayerPairs.filter { (upToDatePlayer, playerOld) ->
            playerOld != null && hasAnyRelevantAttributeChanged(playerOld, upToDatePlayer)
        }.map { (upToDatePlayer, playerOld) -> Pair(upToDatePlayer, playerOld!!) }

        if (playersToUpdate.isNotEmpty()) {
            Logger.trace { "Players to update: $playersToUpdate" }
            playersToUpdate.forEach { (upToDatePlayer, playerOld) ->
                store.updatePlayerWithConnectionStateAndReady(
                    playerOld.id,
                    upToDatePlayer.connectionState,
                    upToDatePlayer.ready
                )
            }
        } else {
            Logger.trace { "No player to update." }
        }
    }

    private fun hasAnyRelevantAttributeChanged(playerOld: Player, upToDatePlayer: PlayerWr): Boolean {
        return playerOld.ready != upToDatePlayer.ready || playerOld.connectionState != upToDatePlayer.connectionState
    }

    private fun addNewPlayers(playersUpToDate: List<PlayerWr>, playersOld: List<Player>) {
        val playersToAdd = playersUpToDate.filter { player ->
            val playerToAdd = playersOld.find { p -> p.dwitchId == player.dwitchId }
            playerToAdd == null
        }

        if (playersToAdd.isNotEmpty()) {
            Logger.trace { "Players to add: $playersToAdd" }
            store.insertPlayers(
                playersToAdd.map { p ->
                    Player(
                        0,
                        p.dwitchId,
                        0,
                        p.name,
                        p.playerRole,
                        p.connectionState,
                        p.ready
                    )
                }
            )
        } else {
            Logger.trace { "No player to add." }
        }
    }
}
