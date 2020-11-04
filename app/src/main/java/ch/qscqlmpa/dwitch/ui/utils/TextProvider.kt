package ch.qscqlmpa.dwitch.ui.utils

import android.content.Context
import javax.inject.Inject

class TextProvider @Inject constructor(private val context: Context){

    fun getText(resourceId: Int): String {
        return context.getString(resourceId)
    }
}