package ch.qscqlmpa.dwitchgame.ingame.gameevents

sealed class GuestGameEvent {
    object KickedOffGame : GuestGameEvent()
    object GameCanceled : GuestGameEvent()
    object GameLaunched : GuestGameEvent()
    object GameOver : GuestGameEvent()
}
