package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class WaitingRoomStateUpdateMessageProcessor @Inject constructor(private val store: InGameStore) :
    MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.WaitingRoomStateUpdateMessage

        return Completable.fromCallable {

            val playersInWr = store.getPlayersInWaitingRoom()

            removePlayersWhoLeftTheGame(msg.playerList, playersInWr)
            updatePlayersWhoseStateChanged(msg.playerList, playersInWr)
            addNewPlayers(msg.playerList, playersInWr)
        }
    }

    private fun removePlayersWhoLeftTheGame(playersUpToDate: List<Player>, playersOld: List<Player>) {
        val playersToRemove = playersOld.map(Player::inGameId).toHashSet()
        playersToRemove.removeAll(playersUpToDate.map(Player::inGameId))
        if (playersToRemove.size > 0) {
            Timber.v("Players to remove: $playersToRemove")
            store.deletePlayers(getLocalIdOfPlayersToRemove(playersOld, playersToRemove))
        } else {
            Timber.v("No player to remove.")
        }
    }

    private fun getLocalIdOfPlayersToRemove(playersOld: List<Player>, playersToRemove: Set<PlayerInGameId>): List<Long> {
        return playersOld.filter { p -> playersToRemove.contains(p.inGameId) }.map { p -> p.id }
    }

    private fun updatePlayersWhoseStateChanged(upToDatePlayers: List<Player>, playersOld: List<Player>) {

        val upToDateOldPlayerPairs = upToDatePlayers.map { upToDatePlayer ->
            val playerOld = playersOld.find { playerOld -> playerOld.inGameId == upToDatePlayer.inGameId }
            Pair(upToDatePlayer, playerOld)
        }

        val playersToUpdate = upToDateOldPlayerPairs.filter { (upToDatePlayer, playerOld) ->
            playerOld != null && hasAnyRelevantAttributeChanged(playerOld, upToDatePlayer)
        }.map { (upToDatePlayer, playerOld) ->
            upToDatePlayer.copy(id = playerOld!!.id) // Replace ID since this is local store specific
        }

        if (playersToUpdate.isNotEmpty()) {
            Timber.v("Players to update: $playersToUpdate")
            playersToUpdate.forEach { player -> store.updatePlayerWithConnectionStateAndReady(player.id, player.connectionState, player.ready) }
        } else {
            Timber.v("No player to update.")
        }
    }

    private fun hasAnyRelevantAttributeChanged(playerOld: Player, upToDatePlayer: Player): Boolean {
        return playerOld.ready != upToDatePlayer.ready || playerOld.connectionState != upToDatePlayer.connectionState
    }

    private fun addNewPlayers(playersUpToDate: List<Player>, playersOld: List<Player>) {
        val playersToAdd = playersUpToDate.filter { player ->
            val playerToAdd = playersOld.find { p -> p.inGameId == player.inGameId }
            playerToAdd == null
        }

        if (playersToAdd.isNotEmpty()) {
            Timber.v("Players to add: $playersToAdd")
            playersToAdd.forEach { player -> store.insertNonLocalPlayer(player) }
        } else {
            Timber.v("No player to add.")
        }
    }
}