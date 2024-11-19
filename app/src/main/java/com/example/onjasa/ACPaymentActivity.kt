package com.example.onjasa


import android.content.Intent
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

    // Mendeklarasikan variabel untuk menampung data yang diterima
    private var username: String? = null
    private var technicianName: String? = null
    private var technicianPrice: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acpayment)

        // Mengambil data dari Intent yang dikirim oleh LoadingOrderActivity
        username = intent.getStringExtra("USERNAME")
        technicianName = intent.getStringExtra("TECHNICIAN_NAME")
        technicianPrice = intent.getDoubleExtra("TECHNICIAN_PRICE", 0.0)

        // Menambahkan komentar pada bagian lama untuk memberi penjelasan
        /*
        // Inisialisasi SDK Midtrans
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-TdLRuoPrprl724bG") // Ganti dengan Client Key yang sesuai
            .setContext(applicationContext)
            .setTransactionFinishedCallback { result ->
                handleTransactionResult(result)
            }
            .setMerchantBaseUrl("https://euryfgpcdw.loclx.io/charge/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()
        */

        // Fungsi untuk menangani klik tombol "Bayar"
        val btnBayar: Button = findViewById(R.id.btnBayar)
        btnBayar.setOnClickListener {
            // Hanya mengirimkan data intent ke MapViewActivity
            val intent = Intent(this, MapViewActivity::class.java).apply {
                putExtra("USERNAME", username) // Mengirimkan username
                putExtra("TECHNICIAN_NAME", technicianName) // Mengirimkan nama teknisi
                putExtra("TECHNICIAN_PRICE", technicianPrice) // Mengirimkan harga teknisi
            }
            // Memulai MapViewActivity dengan membawa data
            startActivity(intent)
        }
    }

    // Fungsi untuk menangani hasil transaksi (dikosongkan sementara)
    /*
    private fun handleTransactionResult(result: TransactionResult?) {
        when (result?.status) {
            TransactionResult.STATUS_SUCCESS -> Toast.makeText(this, "Transaksi Sukses", Toast.LENGTH_LONG).show()
            TransactionResult.STATUS_PENDING -> Toast.makeText(this, "Transaksi Tertunda", Toast.LENGTH_LONG).show()
            TransactionResult.STATUS_FAILED -> Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "Transaksi Dibatalkan atau Tidak Valid", Toast.LENGTH_LONG).show()
        }
    }
    */

}

