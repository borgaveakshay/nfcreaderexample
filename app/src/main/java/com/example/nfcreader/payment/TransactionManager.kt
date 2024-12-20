package com.example.nfcreader.payment

import com.example.nfcreader.nfc.CardType
import com.example.nfcreader.nfc.NFCCardReader.CardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.util.*

class TransactionManager {
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState

    fun processTransaction(amount: BigDecimal, cardData: CardData): TransactionResult {
        // Simulate transaction processing
        return TransactionResult(
            id = UUID.randomUUID().toString(),
            amount = amount,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            cardType = cardData.cardType,
            lastFourDigits = cardData.lastFourDigits
        )
    }
}

sealed class TransactionState {
    object Idle : TransactionState()
    object Processing : TransactionState()
    data class Completed(val result: TransactionResult) : TransactionState()
    data class Failed(val error: String) : TransactionState()
}

data class TransactionResult(
    val id: String,
    val amount: BigDecimal,
    val status: TransactionStatus,
    val timestamp: Long,
    val cardType: CardType,
    val lastFourDigits: String?
)

enum class TransactionStatus {
    SUCCESS,
    FAILED,
    DECLINED
} 