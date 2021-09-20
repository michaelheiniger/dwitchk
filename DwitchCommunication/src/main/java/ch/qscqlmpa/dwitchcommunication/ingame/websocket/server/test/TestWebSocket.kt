package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test

import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.enums.Opcode
import org.java_websocket.enums.ReadyState
import org.java_websocket.framing.Framedata
import org.java_websocket.protocols.IProtocol
import org.tinylog.kotlin.Logger
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import javax.net.ssl.SSLSession

internal class TestWebSocket(private val remoteAddress: String, private val remotePort: Int) : WebSocket {

    override fun sendFragmentedFrame(op: Opcode?, buffer: ByteBuffer?, fin: Boolean) {
        throw NotImplementedError()
    }

    override fun hasSSLSupport(): Boolean {
        throw NotImplementedError()
    }

    override fun getSSLSession(): SSLSession {
        throw NotImplementedError()
    }

    override fun closeConnection(code: Int, message: String?) {
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

    override fun getReadyState(): ReadyState {
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
        Logger.info { "WebSocket (remoteAddress: $remoteAddress,  remotePort: $remotePort) close() called." }
    }

    override fun isClosed(): Boolean {
        throw NotImplementedError()
    }

    override fun getProtocol(): IProtocol? {
        return null
    }
}
