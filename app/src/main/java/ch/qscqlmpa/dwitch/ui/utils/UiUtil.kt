package ch.qscqlmpa.dwitch.ui.utils

import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.model.Visibility

object UiUtil {

//    fun LiveData<UiControlModel>.updateView(view: View, lifecycleOwner: LifecycleOwner) {
//        this.observe(
//            lifecycleOwner,
//            { model ->
//                view.visibility = mapToAndroidVisibility(model.visibility)
//                view.isEnabled = model.enabled
//            }
//        )
//    }

    fun LiveData<UiCheckboxModel>.updateCheckbox(checkbox: CheckBox, lifecycleOwner: LifecycleOwner) {
        this.observe(
            lifecycleOwner,
            { model ->
                checkbox.isEnabled = model.enabled
                checkbox.isChecked = model.checked
            }
        )
    }

    private fun mapToAndroidVisibility(visibility: Visibility): Int {
        return when (visibility) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
    }
}
