package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.nfcreader.ui.theme.NFCReaderTheme
import com.example.nfcreader.nfc.NFCCardReader
import com.example.nfcreader.utils.HexUtils.bytesToHex

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val viewModel: NFCViewModel by viewModels()
    
    // Intent filters for handling NFC intents
    private val intentFiltersArray: Array<IntentFilter> by lazy {
        arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
    }

    // Supported NFC technologies
    private val techListsArray: Array<Array<String>> by lazy {
        arrayOf(
            arrayOf(
                IsoDep::class.java.name,
                NfcA::class.java.name
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        
        // Create a PendingIntent for NFC intent handling
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        setContent {
            NFCReaderTheme {
                when {
                    nfcAdapter == null -> {
                        ErrorScreen(getString(R.string.error_nfc_not_supported))
                    }
                    !nfcAdapter?.isEnabled!! -> {
                        ErrorScreen(getString(R.string.error_nfc_disabled))
                    }
                    else -> {
                        NFCCardScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable foreground dispatch for NFC
        nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            intentFiltersArray,
            techListsArray
        )
    }

    override fun onPause() {
        super.onPause()
        // Disable foreground dispatch
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        when (intent.action) {
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
                } else {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                }
                processNFCTag(tag)
            }
        }
    }
    private fun processNFCTag(tag: Tag?) {
        viewModel.updateNFCStatus(NFCStatus.Reading)
        
        tag?.let {
            try {
                // Try to get an IsoDep instance for the tag
                val isoDep = IsoDep.get(tag)
                if (isoDep != null) {
                    readIsoDepCard(isoDep)
                } else {
                    // Try other technologies if IsoDep is not supported
                    readBasicTagInfo(tag)
                }
            } catch (e: Exception) {
                Log.e("NFC", "Error reading NFC card", e)
                viewModel.updateNFCStatus(NFCStatus.Error(e.message ?: "Unknown error"))
            }
        } ?: viewModel.updateNFCStatus(NFCStatus.Error("No tag detected"))
    }

    private fun readIsoDepCard(isoDep: IsoDep) {
        try {
            val cardReader = NFCCardReader(isoDep)
            val cardData = cardReader.readCard()
            viewModel.updateCardData(cardData)
        } catch (e: Exception) {
            Log.e("NFC", "Error reading card", e)
            viewModel.updateNFCStatus(NFCStatus.Error("Error reading card: ${e.message}"))
        }
    }

    private fun readBasicTagInfo(tag: Tag) {
        val id = tag.id
        val technologies = tag.techList
        val info = buildString {
            append("Tag ID: ${bytesToHex(id)}\n")
            append("Technologies: ${technologies.joinToString(", ")}")
        }
        viewModel.updateNFCStatus(NFCStatus.Success(info))
    }
} 