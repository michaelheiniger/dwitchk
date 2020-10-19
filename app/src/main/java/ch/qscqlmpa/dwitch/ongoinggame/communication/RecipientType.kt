package ch.qscqlmpa.dwitch.ongoinggame.communication

sealed class RecipientType {
    data class Single(val localId: LocalConnectionId) : RecipientType()
    object All : RecipientType()
}