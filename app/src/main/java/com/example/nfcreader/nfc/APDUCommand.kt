package com.example.nfcreader.nfc

data class APDUCommand(
    val cla: Byte = 0x00,
    val ins: Byte,
    val p1: Byte = 0x00,
    val p2: Byte = 0x00,
    val data: ByteArray = ByteArray(0),
    val le: Int? = null
) {
    fun toByteArray(): ByteArray {
        val command = mutableListOf<Byte>()
        command.add(cla)
        command.add(ins)
        command.add(p1)
        command.add(p2)

        if (data.isNotEmpty()) {
            command.add(data.size.toByte())
            data.forEach { command.add(it) }
        }

        le?.let { command.add(it.toByte()) }

        return command.toByteArray()
    }
}

data class APDUResponse(
    val data: ByteArray,
    val sw1: Int,
    val sw2: Int
) {
    val isSuccess: Boolean get() = sw1 == 0x90 && sw2 == 0x00

    companion object {
        fun fromByteArray(bytes: ByteArray): APDUResponse {
            val data = bytes.dropLast(2).toByteArray()
            val sw1 = bytes[bytes.size - 2].toInt() and 0xFF
            val sw2 = bytes[bytes.size - 1].toInt() and 0xFF
            return APDUResponse(data, sw1, sw2)
        }
    }
} 