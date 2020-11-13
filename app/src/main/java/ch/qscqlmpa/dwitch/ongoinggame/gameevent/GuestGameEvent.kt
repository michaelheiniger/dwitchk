package ch.qscqlmpa.dwitch.ongoinggame.gameevent

sealed class GuestGameEvent {
    object GameCanceled : GuestGameEvent()
    object GameLaunched : GuestGameEvent()
    object GameOver : GuestGameEvent()
}