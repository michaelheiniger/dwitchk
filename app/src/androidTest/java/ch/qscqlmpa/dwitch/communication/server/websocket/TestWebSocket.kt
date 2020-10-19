package ch.qscqlmpa.dwitch.communication.server.websocket

import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.framing.Framedata
import java.net.InetSocketAddress
import java.nio.ByteBuffer

internal class TestWebSocket(private val remoteAddress: String, private val remotePort: Int) : WebSocket {
    override fun sendFragmentedFrame(op: Framedata.Opcode?, buffer: ByteBuffer?, fin: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeConnection(code: Int, message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isConnecting(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClosing(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemoteSocketAddress(): InetSocketAddress {
        return InetSocketAddress(remoteAddress, remotePort)
    }

    override fun <T : Any?> getAttachment(): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendFrame(framedata: Framedata?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendFrame(frames: MutableCollection<Framedata>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasBufferedData(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReadyState(): WebSocket.READYSTATE {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any?> setAttachment(attachment: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalSocketAddress(): InetSocketAddress {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendPing() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDraft(): Draft {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourceDescriptor(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFlushAndClose(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun send(text: String?) {
        // Nothing to do
    }

    override fun send(bytes: ByteBuffer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun send(bytes: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close(code: Int, message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close(code: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isClosed(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}