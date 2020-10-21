package ch.qscqlmpa.dwitch.ongoinggame.communication

import kotlinx.serialization.Serializable

@Serializable
data class Envelope(val message: String)
