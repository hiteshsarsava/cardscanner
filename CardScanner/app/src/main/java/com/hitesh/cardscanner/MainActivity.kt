package com.hitesh.cardscanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.hitesh.cardscanner.databinding.ActivityMainBinding
import com.hitesh.mylibrary.model.CardDetails
import com.hitesh.mylibrary.ui.ScannerActivity

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent = result.data ?: return@registerForActivityResult
                val cardDetails: CardDetails =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data.getSerializableExtra(
                            ScannerActivity.CARD_DETAILS,
                            CardDetails::class.java
                        ) as CardDetails
                    } else {
                        data.getSerializableExtra(ScannerActivity.CARD_DETAILS) as CardDetails
                    }

                setCardData(cardDetails)

            }
        }

    @SuppressLint("SetTextI18n")
    private fun setCardData(cardDetails: CardDetails) {
        binding.apply {
            cardView.visibility = View.VISIBLE

            tvCardNo.text = cardDetails.number
            tvOwnerName.text = cardDetails.owner
            tvExpiryDate.text = "${cardDetails.expirationMonth}/${cardDetails.expirationYear}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStartScan.setOnClickListener {
                cardView.visibility = View.GONE
                resultLauncher.launch(Intent(this@MainActivity, ScannerActivity::class.java))
            }
        }
    }
}