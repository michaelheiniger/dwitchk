package ch.qscqlmpa.dwitchcommunication.model

import kotlinx.serialization.Serializable

@Serializable
data class Envelope(val message: String)
