package ch.qscqlmpa.dwitchgame.ongoinggame.gameevents

sealed class GuestGameEvent {
    object GameCanceled : GuestGameEvent()
    object GameLaunched : GuestGameEvent()
    object GameOver : GuestGameEvent()
}
