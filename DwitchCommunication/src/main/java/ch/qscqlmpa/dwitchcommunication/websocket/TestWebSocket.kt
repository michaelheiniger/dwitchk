package ch.qscqlmpa.dwitchcommunication.websocket

import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.framing.Framedata
import java.net.InetSocketAddress
import java.nio.ByteBuffer

internal class TestWebSocket(private val remoteAddress: String, private val remotePort: Int) : WebSocket {
    override fun sendFragmentedFrame(op: Framedata.Opcode?, buffer: ByteBuffer?, fin: Boolean) {
        throw NotImplementedError()
    }

    override fun closeConnection(code: Int, message: String?) {
        throw NotImplementedError()
    }

    override fun isConnecting(): Boolean {
        throw NotImplementedError()
    }

    override fun isClosing(): Boolean {
        throw NotImplementedError()
    }

    override fun getRemoteSocketAddress(): InetSocketAddress {
        return InetSocketAddress(remoteAddress, remotePort)
    }

    override fun <T : Any?> getAttachment(): T {
        throw NotImplementedError()
    }

    override fun sendFrame(framedata: Framedata?) {
        throw NotImplementedError()
    }

    override fun sendFrame(frames: MutableCollection<Framedata>?) {
        throw NotImplementedError()
    }

    override fun hasBufferedData(): Boolean {
        throw NotImplementedError()
    }

    override fun getReadyState(): WebSocket.READYSTATE {
        throw NotImplementedError()
    }

    override fun <T : Any?> setAttachment(attachment: T) {
        throw NotImplementedError()
    }

    override fun isOpen(): Boolean {
        throw NotImplementedError()
    }

    override fun getLocalSocketAddress(): InetSocketAddress {
        throw NotImplementedError()
    }

    override fun sendPing() {
        throw NotImplementedError()
    }

    override fun getDraft(): Draft {
        throw NotImplementedError()
    }

    override fun getResourceDescriptor(): String {
        throw NotImplementedError()
    }

    override fun isFlushAndClose(): Boolean {
        throw NotImplementedError()
    }

    override fun send(text: String?) {
        // Nothing to do
    }

    override fun send(bytes: ByteBuffer?) {
        throw NotImplementedError()
    }

    override fun send(bytes: ByteArray?) {
        throw NotImplementedError()
    }

    override fun close(code: Int, message: String?) {
        throw NotImplementedError()
    }

    override fun close(code: Int) {
        throw NotImplementedError()
    }

    override fun close() {
        throw NotImplementedError()
    }

    override fun isClosed(): Boolean {
        throw NotImplementedError()
    }


}