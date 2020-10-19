package ch.qscqlmpa.dwitch.ongoinggame

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableOnSubscribe
import timber.log.Timber

/**
 * Adapted from https://gist.github.com/MisterRager/3d40e4be8aa4624084528a49c803d6a5
 */
class ServiceBindingFactory {

    companion object {

        fun bind(context: Context, intent: Intent, flags: Int): Completable {
            return Completable.using(
                    { Connection() },
                    { con: Connection ->
                        context.bindService(intent, con as ServiceConnection, flags)
                        Completable.create(con)
                    }, { conn: Connection -> context.unbindService(conn) })
        }

        private class Connection : ServiceConnection, CompletableOnSubscribe {

            private lateinit var subscriber: CompletableEmitter

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Timber.d("onServiceConnected()")
                subscriber.onComplete()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                // Nothing to do
            }

            @Throws(Exception::class)
            override fun subscribe(observableEmitter: CompletableEmitter) {
                subscriber = observableEmitter
            }
        }
    }
}