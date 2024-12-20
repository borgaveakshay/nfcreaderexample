package com.example.nfcreader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactlessIcon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfcreader.nfc.NFCCardReader
import com.example.nfcreader.utils.collectAsLifecycleAware

@Composable
fun NFCCardScreen(viewModel: NFCViewModel) {
    val nfcStatus by viewModel.nfcStatus.collectAsLifecycleAware()
    val cardData by viewModel.cardData.collectAsLifecycleAware()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "MY CARDS",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )

            when (nfcStatus) {
                is NFCStatus.Waiting -> {
                    EmptyCardState("Please tap your card")
                }
                is NFCStatus.Reading -> {
                    EmptyCardState("Reading card...")
                }
                is NFCStatus.Success -> {
                    cardData?.let { data ->
                        CardDisplay(data)
                    }
                }
                is NFCStatus.Error -> {
                    EmptyCardState(
                        message = (nfcStatus as NFCStatus.Error).message,
                        isError = true
                    )
                }
            }
        }
    }
}

@Composable
fun CardDisplay(cardData: NFCCardReader.CardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A237E) // Dark blue color for card background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.ContactlessIcon,
                    contentDescription = "Contactless",
                    tint = Color.White
                )
                Text(
                    text = cardData.cardType.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Card Number
            Text(
                text = cardData.lastFourDigits?.let { 
                    "**** **** **** $it"
                } ?: "**** **** **** ****",
                color = Color.White,
                fontSize = 18.sp,
                letterSpacing = 2.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cardholder Name
                Column {
                    Text(
                        text = "CARDHOLDER",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = cardData.cardholderName ?: "CARD HOLDER",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                // Expiry Date
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = cardData.expiryDate ?: "MM/YY",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCardState(message: String, isError: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isError) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = message,
                color = if (isError) MaterialTheme.colorScheme.error 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
} 