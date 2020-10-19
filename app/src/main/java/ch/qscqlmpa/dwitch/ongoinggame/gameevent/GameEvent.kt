package ch.qscqlmpa.dwitch.ongoinggame.gameevent

sealed class GameEvent {
    object GameCanceled : GameEvent()
}