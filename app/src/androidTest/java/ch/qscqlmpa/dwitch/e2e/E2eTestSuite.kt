package ch.qscqlmpa.dwitch.e2e

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    HomeTest::class,
    JoinNewGameTest::class,
    HostNewGameTest::class,
    WaitingRoomAsGuestTest::class,
    WaitingRoomAsHostTest::class,
    GameRoomAsGuestTest::class,
    GameRoomAsHostTest::class
)
class E2eTestSuite
