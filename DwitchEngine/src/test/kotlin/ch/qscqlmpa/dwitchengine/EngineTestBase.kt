package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import org.junit.jupiter.api.BeforeEach

abstract class EngineTestBase {

    protected val p1 = TestEntityFactory.createGuestPlayer1Info()
    protected val p2 = TestEntityFactory.createGuestPlayer2Info()
    protected val p3 = TestEntityFactory.createGuestPlayer3Info()
    protected val p4 = TestEntityFactory.createGuestPlayer4Info()
    protected val p5 = TestEntityFactory.createGuestPlayer5Info()
    protected val p1Id = p1.id
    protected val p2Id = p2.id
    protected val p3Id = p3.id
    protected val p4Id = p4.id
    protected val p5Id = p5.id

    protected lateinit var gameStateBuilder: EngineTestGameStateBuilder
    protected lateinit var initialGameState: DwitchGameState
    protected lateinit var gameStateUpdated: DwitchGameState

    @BeforeEach
    open fun setup() {
        gameStateBuilder = EngineTestGameStateBuilder()
    }
}
