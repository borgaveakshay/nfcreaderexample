package com.example.nfcreader.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import com.example.nfcreader.utils.HexUtils.bytesToHex
import com.example.nfcreader.utils.HexUtils.hexToByteArray

class NFCCardReader(private val isoDep: IsoDep) {
    companion object {
        private const val TAG = "NFCCardReader"
        
        // Standard AIDs for payment cards
        private val KNOWN_AIDS = arrayOf(
            "A0000000031010", // VISA
            "A0000000041010", // Mastercard
            "A000000025",     // American Express
            "A0000000651010", // JCB
            "A0000003241010"  // Discover
        )

        // APDU Commands
        private const val GET_PROCESSING_OPTIONS = "80A8000002830000"
        private const val READ_RECORD = "00B2010C00"
    }

    data class CardData(
        val cardType: CardType = CardType.UNKNOWN,
        val aid: String = "",
        val lastFourDigits: String? = null,
        val expiryDate: String? = null,
        val cardholderName: String? = null
    )

    fun readCard(): CardData {
        try {
            isoDep.connect()
            
            // Try to select each known AID
            for (aid in KNOWN_AIDS) {
                val response = selectAID(aid)
                if (response.isSuccess) {
                    val cardType = CardType.fromAID(aid)
                    return processCardResponse(cardType, aid, response.data)
                }
            }
            
            return CardData(CardType.UNKNOWN, "Unknown")
        } finally {
            try {
                isoDep.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing IsoDep", e)
            }
        }
    }

    private fun selectAID(aid: String): APDUResponse {
        val aidBytes = hexToByteArray(aid)
        val command = APDUCommand(
            cla = 0x00,
            ins = 0xA4.toByte(),
            p1 = 0x04,
            p2 = 0x00,
            data = aidBytes
        )
        return sendCommand(command)
    }

    private fun processCardResponse(cardType: CardType, aid: String, responseData: ByteArray): CardData {
        // Process card-specific data based on the card type
        return when (cardType) {
            CardType.VISA -> processVisaCard(aid, responseData)
            CardType.MASTERCARD -> processMastercardCard(aid, responseData)
            else -> CardData(cardType, aid)
        }
    }

    private fun sendCommand(command: APDUCommand): APDUResponse {
        val response = isoDep.transceive(command.toByteArray())
        return APDUResponse.fromByteArray(response)
    }

    private fun processVisaCard(aid: String, responseData: ByteArray): CardData {
        try {
            // Get Processing Options
            val gpoResponse = sendCommand(APDUCommand(
                cla = 0x80.toByte(),
                ins = 0xA8.toByte(),
                p1 = 0x00,
                p2 = 0x00,
                data = hexToByteArray("8300")
            ))

            if (!gpoResponse.isSuccess) {
                return CardData(CardType.VISA, aid)
            }

            // Read Record
            val readRecordResponse = sendCommand(APDUCommand(
                cla = 0x00,
                ins = 0xB2.toByte(),
                p1 = 0x01,
                p2 = 0x0C,
                le = 0x00
            ))

            if (!readRecordResponse.isSuccess) {
                return CardData(CardType.VISA, aid)
            }

            // Parse response data
            val recordData = bytesToHex(readRecordResponse.data)
            
            // Extract card info (this is a simplified example)
            val lastFourDigits = extractLastFourDigits(recordData)
            val expiryDate = extractExpiryDate(recordData)
            val cardholderName = extractCardholderName(recordData)

            return CardData(
                cardType = CardType.VISA,
                aid = aid,
                lastFourDigits = lastFourDigits,
                expiryDate = expiryDate,
                cardholderName = cardholderName
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Visa card", e)
            return CardData(CardType.VISA, aid)
        }
    }

    private fun processMastercardCard(aid: String, responseData: ByteArray): CardData {
        try {
            // Similar to Visa but with Mastercard-specific processing
            val gpoResponse = sendCommand(APDUCommand(
                cla = 0x80.toByte(),
                ins = 0xA8.toByte(),
                p1 = 0x00,
                p2 = 0x00,
                data = hexToByteArray("8300")
            ))

            if (!gpoResponse.isSuccess) {
                return CardData(CardType.MASTERCARD, aid)
            }

            // Read Record
            val readRecordResponse = sendCommand(APDUCommand(
                cla = 0x00,
                ins = 0xB2.toByte(),
                p1 = 0x01,
                p2 = 0x0C,
                le = 0x00
            ))

            if (!readRecordResponse.isSuccess) {
                return CardData(CardType.MASTERCARD, aid)
            }

            // Parse response data
            val recordData = bytesToHex(readRecordResponse.data)
            
            // Extract card info (this is a simplified example)
            val lastFourDigits = extractLastFourDigits(recordData)
            val expiryDate = extractExpiryDate(recordData)
            val cardholderName = extractCardholderName(recordData)

            return CardData(
                cardType = CardType.MASTERCARD,
                aid = aid,
                lastFourDigits = lastFourDigits,
                expiryDate = expiryDate,
                cardholderName = cardholderName
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Mastercard card", e)
            return CardData(CardType.MASTERCARD, aid)
        }
    }

    private fun extractLastFourDigits(recordData: String): String? {
        // This is a simplified example. In reality, you would need to parse
        // the TLV (Tag-Length-Value) data according to EMV specifications
        return try {
            // Look for PAN (Primary Account Number) in the record data
            // This is just an example and won't work with real cards
            recordData.takeLast(8).take(4)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractExpiryDate(recordData: String): String? {
        return try {
            // Look for expiry date in the record data
            // Format: YYMM
            // This is just an example and won't work with real cards
            val date = recordData.substring(recordData.length - 12, recordData.length - 8)
            "${date.substring(2, 4)}/${date.substring(0, 2)}"
        } catch (e: Exception) {
            null
        }
    }

    private fun extractCardholderName(recordData: String): String? {
        return try {
            // Look for cardholder name in the record data
            // This is just an example and won't work with real cards
            "CARD HOLDER" // Placeholder
        } catch (e: Exception) {
            null
        }
    }
} 