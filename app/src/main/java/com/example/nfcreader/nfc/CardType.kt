package com.example.nfcreader.nfc

enum class CardType {
    VISA,
    MASTERCARD,
    AMEX,
    OTHER,
    UNKNOWN;

    companion object {
        fun fromAID(aid: String): CardType = when {
            aid.startsWith("A0000000031010") -> VISA
            aid.startsWith("A0000000041010") -> MASTERCARD
            aid.startsWith("A000000025") -> AMEX
            else -> UNKNOWN
        }
    }
} 