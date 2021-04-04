package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.uitests.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    HomeTest::class,
    NewGameAsGuestTest::class,
    NewGameAsHostTest::class,
    WaitingRoomAsGuestTest::class,
    WaitingRoomAsHostTest::class,
    GameRoomAsGuestTest::class,
    GameRoomAsHostTest::class
)
class UiTestSuite
