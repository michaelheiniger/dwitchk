package ch.qscqlmpa.dwitch.ui.utils

import android.view.View
import ch.qscqlmpa.dwitch.ui.model.Visibility

object UiUtil {

    fun mapToAndroidVisibility(visibility: Visibility): Int {
        return when (visibility) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
    }
}