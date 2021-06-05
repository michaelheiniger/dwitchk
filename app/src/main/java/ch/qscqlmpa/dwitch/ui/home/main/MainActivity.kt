package ch.qscqlmpa.dwitch.ui.home.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import ch.qscqlmpa.dwitch.ui.DwitchApp
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity

class MainActivity : HomeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DwitchApp(viewModelFactory) }
    }

    //TODO: Remove when no longer used
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
