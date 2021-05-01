package ch.qscqlmpa.dwitchmodel.player

/**
 * Used to distinguish whether the app should run as the guest or as the host in the game
 */
enum class PlayerRole {
    GUEST,
    HOST;

    fun isHost(): Boolean {
        return this == HOST
    }
}
