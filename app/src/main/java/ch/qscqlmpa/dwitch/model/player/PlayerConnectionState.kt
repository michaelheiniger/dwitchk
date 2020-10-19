package ch.qscqlmpa.dwitch.model.player

enum class PlayerConnectionState {
    /**
     * An active connection exists between the Host and the Guest.
     */
    CONNECTED,

    /**
     * No active connection exists between the Host and the Guest. Used for instanced if the connection has been temporarily lost.
     */
    DISCONNECTED
}