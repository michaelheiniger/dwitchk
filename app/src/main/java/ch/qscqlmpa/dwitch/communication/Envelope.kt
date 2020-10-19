package ch.qscqlmpa.dwitch.communication

import kotlinx.serialization.Serializable

@Serializable
data class Envelope(val message: String)
