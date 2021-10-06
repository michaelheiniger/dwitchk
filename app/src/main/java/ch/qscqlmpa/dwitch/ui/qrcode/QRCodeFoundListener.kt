package ch.qscqlmpa.dwitch.ui.qrcode

interface QRCodeFoundListener {
    fun onQRCodeFound(qrCode: String?)
    fun qrCodeNotFound()
}
