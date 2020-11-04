package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.Host
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.IntTestWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat

class IntTestHost(
    private val gameName: String,
    private val networkHub: NetworkHub
) : IntTestPlayer() {

    private var hostLocalId: Long? = null
    lateinit var playerId: PlayerInGameId

    fun createGame() {
        appComponent.newGameUsecase.hostNewgame(gameName, Host.name).blockingGet()
        val game = appComponent.database.gameDao().getGameByName(gameName)
            ?: throw IllegalStateException("New game can't be fetched from store")
        gameLocalId = game.id
        hostLocalId = game.localPlayerLocalId
        playerId = appComponent.database.playerDao().getLocalPlayer(hostLocalId!!).inGameId

        hookOnGoingGameComponent()
        hookupHostToNetworkHub()
    }

    fun gameCommonId(): GameCommonId {
        return ongoingGameComponent.inGameStore.getGame().gameCommonId
    }

    fun launchGame() {
        val gameLaunchableEvent = ongoingGameComponent.gameLaunchableUsecase.gameCanBeLaunched().blockingFirst()
        assertThat(gameLaunchableEvent).isEqualTo(GameLaunchableEvent.GameIsReadyToBeLaunched)

        // Order is according to their players' name (alphabetical, ascending)
        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(DeterministicInitialGameSetup(
                mapOf(
                    0 to listOf(Card.Clubs2, Card.Clubs3), // Host
                    1 to listOf(Card.Diamonds6, Card.Hearts4), // Guest1
                    2 to listOf(Card.Diamonds7, Card.Spades3) // Guest2
                ),
                mapOf(
                    0 to Rank.Asshole,
                    1 to Rank.Neutral,
                    2 to Rank.President
                )
            ))

        ongoingGameComponent.launchGameUsecase.launchGame()
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .blockingGet()
    }

    fun endGame() {
        ongoingGameComponent.endGameUsecase.endGame().blockingGet()
    }

    private fun hookupHostToNetworkHub() {
        val websocket = ongoingGameComponent.websocketServer as IntTestWebsocketServer
        networkHub.setHost(websocket)
        websocket.setNetworkHub(networkHub)
    }
}