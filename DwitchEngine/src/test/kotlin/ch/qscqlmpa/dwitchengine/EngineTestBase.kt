package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.GameState
import org.junit.jupiter.api.BeforeEach

abstract class EngineTestBase {

    protected val player1 = TestEntityFactory.createGuestPlayer1Info()
    protected val player2 = TestEntityFactory.createGuestPlayer2Info()
    protected val player3 = TestEntityFactory.createGuestPlayer3Info()
    protected val player4 = TestEntityFactory.createGuestPlayer4Info()
    protected val player5 = TestEntityFactory.createGuestPlayer5Info()
    protected val player1Id = player1.id
    protected val player2Id = player2.id
    protected val player3Id = player3.id
    protected val player4Id = player4.id
    protected val player5Id = player5.id

    protected lateinit var gameStateBuilder: EngineTestGameStateBuilder
    protected lateinit var initialGameState: GameState
    protected lateinit var gameStateUpdated: GameState

    @BeforeEach
    open fun setup() {
        gameStateBuilder = EngineTestGameStateBuilder()
    }
}
