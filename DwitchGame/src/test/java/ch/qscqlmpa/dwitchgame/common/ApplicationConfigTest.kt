package ch.qscqlmpa.dwitchgame.common

internal fun testApplicationConfig(): ApplicationConfig {
    return ApplicationConfig(
        communication = Communication(waitForJoinOrRejoinAckTimeout = 5)
    )
}
