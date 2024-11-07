package com.example.onjasa

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder


class ACPaymentActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acpayment)

        // Inisialisasi SDK Midtrans menggunakan UiKitApi.Builder()
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-TdLRuoPrprl724bG")
            .setContext(applicationContext)
            .setTransactionFinishedCallback { result ->
                handleTransactionResult(result)
            }
            .setMerchantBaseUrl("https://euryfgpcdw.loclx.io/charge/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()


        // Menangani klik tombol "Bayar"
        val btnBayar: Button = findViewById(R.id.btnBayar)
        btnBayar.setOnClickListener {
            // Ambil nama layanan dan harga layanan dari TextView
            val namaLayanan = findViewById<TextView>(R.id.namaLayanan).text.toString()
            val hargaLayanan = findViewById<TextView>(R.id.hargaLayanan).text.toString()
                .replace(Regex("[^\\d]"), "") // Menghapus semua karakter non-digit
                .toIntOrNull() ?: 0 // Mengonversi ke Int atau default 0 jika gagal

            if (hargaLayanan <= 0) {
                Toast.makeText(this, "Harga layanan tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat request transaksi untuk Midtrans
            val transactionRequest = TransactionRequest("OnJasa-${System.currentTimeMillis()}", hargaLayanan.toDouble())
            val itemDetails = ItemDetails("ServiceItemID", hargaLayanan.toDouble(), 1, namaLayanan)
            val itemDetailsList = ArrayList<ItemDetails>()
            itemDetailsList.add(itemDetails)
            transactionRequest.itemDetails = itemDetailsList
            setupCustomerDetails(transactionRequest) // Fungsi untuk setup customer

            // Memulai transaksi di UI Midtrans
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
        }
    }

    private fun setupCustomerDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails().apply {
            customerIdentifier = "RafaelSianturi"
            phone = "081234567890"
            firstName = "Rafael"
            lastName = "Sianturi"
            email = "rafael@example.com"
        }
        transactionRequest.customerDetails = customerDetails
    }

    private fun handleTransactionResult(result: TransactionResult?) {
        when (result?.status) {
            TransactionResult.STATUS_SUCCESS -> Toast.makeText(this, "Transaction Success", Toast.LENGTH_LONG).show()
            TransactionResult.STATUS_PENDING -> Toast.makeText(this, "Transaction Pending", Toast.LENGTH_LONG).show()
            TransactionResult.STATUS_FAILED -> Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "Transaction Canceled or Invalid", Toast.LENGTH_LONG).show()
        }
    }
}
