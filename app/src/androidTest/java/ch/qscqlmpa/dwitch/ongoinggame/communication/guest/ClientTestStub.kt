package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Observable

interface ClientTestStub {

    fun connectClientToServer(enableThreadBreak: Boolean)

    fun serverSendsMessageToClient(message: Message, enableThreadBreak: Boolean)

    fun breakConnectionWithHost()

    /**
     * @return last message sent, if any.
     */
    fun observeMessagesSent(): Observable<String>
}