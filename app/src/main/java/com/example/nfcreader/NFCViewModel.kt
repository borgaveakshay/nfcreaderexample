package com.example.nfcreader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfcreader.nfc.NFCCardReader.CardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NFCViewModel : ViewModel() {
    // StateFlows
    val nfcStatus: StateFlow<NFCStatus>
        field = MutableStateFlow<NFCStatus>(NFCStatus.Waiting)
    val cardData: StateFlow<CardData>
        field = MutableStateFlow<CardData>(CardData())

    fun updateNFCStatus(status: NFCStatus) = viewModelScope.launch {
        nfcStatus.emit(status)
    }

    fun updateCardData(data: CardData) = viewModelScope.launch {
        cardData.emit(data)
        nfcStatus.emit(NFCStatus.Success(buildCardInfoString(data)))
    }

    private fun buildCardInfoString(data: CardData): String = buildString {
        append("Card Type: ${data.cardType}\n")
        if (data.lastFourDigits != null) {
            append("Card Number: **** **** **** ${data.lastFourDigits}\n")
        }
        if (data.expiryDate != null) {
            append("Expires: ${data.expiryDate}\n")
        }
        if (data.cardholderName != null) {
            append("Cardholder: ${data.cardholderName}")
        }
    }
}

sealed class NFCStatus {
    object Waiting : NFCStatus()
    object Reading : NFCStatus()
    data class Success(val cardInfo: String) : NFCStatus()
    data class Error(val message: String) : NFCStatus()
} 