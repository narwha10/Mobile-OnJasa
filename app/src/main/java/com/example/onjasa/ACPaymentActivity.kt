package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class ACPaymentActivity : AppCompatActivity() {

    private var username: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var technicianName: String
    private lateinit var technicianPrice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acpayment)

        db = FirebaseFirestore.getInstance()

        // Mengambil data dari Intent
        username = intent.getStringExtra("USERNAME")
        technicianName = intent.getStringExtra("TECHNICIAN_NAME") ?: "No Technician Name"
        technicianPrice = intent.getStringExtra("TECHNICIAN_PRICE") ?: ""

        val headerTitle = intent.getStringExtra("header_title") ?: "No Title"
        findViewById<TextView>(R.id.jenisLayanan).text = headerTitle
        findViewById<TextView>(R.id.namaTeknisi).text = technicianName
        findViewById<TextView>(R.id.hargaTeknisi).text = technicianPrice
        findViewById<TextView>(R.id.hargaLayanan).text = technicianPrice

        // Inisialisasi SDK Midtrans
        setupMidtransSDK()

        // Tombol bayar
        val btnBayar: Button = findViewById(R.id.btnBayar)
        btnBayar.setOnClickListener {
            handlePayment()
        }
    }

    private fun setupMidtransSDK() {
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-TdLRuoPrprl724bG") // Ganti dengan Client Key yang sesuai
            .setContext(applicationContext)
            .setTransactionFinishedCallback { result ->
                Log.d("MidtransCallback", "Transaction Finished: ${result?.status}")
                handleTransactionResult(result)
            }
            .setMerchantBaseUrl("http://au8hj9p0uz.ap.loclx.io/charge/") // Ganti dengan URL server Anda
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()
    }

    private fun handlePayment() {
        val namaLayanan = findViewById<TextView>(R.id.jenisLayanan).text.toString()
        val hargaLayanan = findViewById<TextView>(R.id.hargaLayanan).text.toString()
            .replace(Regex("[^\\d]"), "") // Hapus karakter non-digit
            .toIntOrNull() ?: 0

        if (hargaLayanan <= 0) {
            Toast.makeText(this, "Harga layanan tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionRequest = TransactionRequest("OnJasa-${System.currentTimeMillis()}", hargaLayanan.toDouble())
        val itemDetails = ItemDetails("ServiceItemID", hargaLayanan.toDouble(), 1, namaLayanan)
        val itemDetailsList = ArrayList<ItemDetails>()
        itemDetailsList.add(itemDetails)
        transactionRequest.itemDetails = itemDetailsList

        setupCustomerDetails(transactionRequest) {
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this) // Mulai pembayaran
        }
    }

    private fun handleTransactionResult(result: TransactionResult?) {
        Log.d("TransactionResult", "Transaction Status: ${result?.status}")

        when (result?.status) {
            TransactionResult.STATUS_SUCCESS -> {
                Log.d("TransactionResult", "Transaction Success")
                Toast.makeText(this, "Transaksi Sukses", Toast.LENGTH_LONG).show()
                navigateToMapView()
            }
            TransactionResult.STATUS_PENDING -> {
                Log.d("TransactionResult", "Transaction Pending")
                Toast.makeText(this, "Transaksi Tertunda. Periksa status pembayaran nanti.", Toast.LENGTH_LONG).show()
            }
            TransactionResult.STATUS_FAILED -> {
                Log.d("TransactionResult", "Transaction Failed")
                Toast.makeText(this, "Transaksi Gagal. Silakan coba lagi.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Log.d("TransactionResult", "Transaction Canceled or Invalid")
                Toast.makeText(this, "Transaksi dibatalkan atau tidak valid.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToMapView() {
        Log.d("Navigation", "Navigating to MapViewActivity")
        val intent = Intent(this, MapViewActivity::class.java).apply {
            putExtra("USERNAME", username)
            putExtra("TECHNICIAN_NAME", technicianName)
            putExtra("TECHNICIAN_PRICE", technicianPrice)
        }
        startActivity(intent)
        finish()
    }


    private fun setupCustomerDetails(
        transactionRequest: TransactionRequest,
        onDetailsReady: () -> Unit // Callback yang dipanggil saat selesai
    ) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.first()
                    val customerDetails = CustomerDetails().apply {
                        customerIdentifier = username
                        phone = userDoc.getString("nohp") ?: "081234567890"
                        firstName = userDoc.getString("firstName") ?: username?.split(" ")?.getOrNull(0) ?: "User"
                        lastName = userDoc.getString("lastName") ?: username?.split(" ")?.getOrNull(1) ?: ""
                        email = userDoc.getString("email") ?: "$username@example.com".replace(" ", "").lowercase()
                    }
                    transactionRequest.customerDetails = customerDetails
                    onDetailsReady() // Callback
                } else {
                    handleMissingUserDetails(transactionRequest, onDetailsReady)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching user details", e)
                handleMissingUserDetails(transactionRequest, onDetailsReady)
            }
    }

    private fun handleMissingUserDetails(
        transactionRequest: TransactionRequest,
        onDetailsReady: () -> Unit
    ) {
        Toast.makeText(this, "Data pengguna tidak ditemukan, menggunakan data default.", Toast.LENGTH_SHORT).show()
        val customerDetails = CustomerDetails().apply {
            customerIdentifier = username
            phone = "081234567890"
            firstName = username?.split(" ")?.getOrNull(0) ?: "User"
            lastName = username?.split(" ")?.getOrNull(1) ?: ""
            email = "$username@example.com".replace(" ", "").lowercase()
        }
        transactionRequest.customerDetails = customerDetails
        onDetailsReady()
    }
}
