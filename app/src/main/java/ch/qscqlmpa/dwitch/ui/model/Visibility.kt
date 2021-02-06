package ch.qscqlmpa.dwitch.ui.model

sealed class Visibility {
    object Visible : Visibility()
    object Invisible : Visibility()
    object Gone : Visibility()
}
