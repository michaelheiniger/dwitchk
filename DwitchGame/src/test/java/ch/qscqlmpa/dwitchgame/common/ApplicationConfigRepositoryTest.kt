package ch.qscqlmpa.dwitchgame.common

internal class ApplicationConfigRepositoryTest {

}

fun testApplicationConfig(): ApplicationConfig {
    return ApplicationConfig(
        GameAdvertising(
            port = 8888
        ),
        Communication(
            waitForJoinOrRejoinAckTimeout = 5
        )
    )
}