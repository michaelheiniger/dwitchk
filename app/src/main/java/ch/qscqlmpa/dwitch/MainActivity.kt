package ch.qscqlmpa.dwitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.Dwitch

class MainActivity : ComponentActivity() {

    val app: App by lazy { (application as App) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dwitch(
                createMainActivityComponent = { navController -> app.createMainActivityComponent(navController) },
                createInGameHostUiComponent = { mainActivityComponent -> app.createInGameHostUiComponent(mainActivityComponent) },
                createInGameGuestUiComponent = { mainActivityComponent -> app.createInGameGuestUiComponent(mainActivityComponent) },
                finish = { finish() }
            )
        }
    }
}
