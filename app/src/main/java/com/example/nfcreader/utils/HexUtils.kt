package com.example.nfcreader.utils

object HexUtils {
    fun hexToByteArray(hexString: String): ByteArray {
        val hex = hexString.replace(" ", "")
        val len = hex.length
        val result = ByteArray(len / 2)

        for (i in 0 until len step 2) {
            result[i / 2] =
                ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
        }

        return result
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }
} 