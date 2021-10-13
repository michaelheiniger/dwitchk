package ch.qscqlmpa.dwitchcommunication.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
object CommunicationModule {

    @CommunicationScope
    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
