package ch.qscqlmpa.dwitchcommunication.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
class CommunicationModule(private val context: Context) {

    @CommunicationScope
    @Provides
    fun provideConnectionManager(): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @CommunicationScope
    @Provides
    fun provideJsonSerializer(): Json {
        return Json.Default
    }
}
