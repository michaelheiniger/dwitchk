package ch.qscqlmpa.dwitch.uitests

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    HomeScreenTest::class,
    NewGameAsGuestTest::class,
    NewGameAsHostTest::class,
    WaitingRoomAsGuestTest::class,
    WaitingRoomAsHostTest::class,
    GameRoomAsGuestTest::class,
    GameRoomAsHostTest::class
)
class UiTestSuite