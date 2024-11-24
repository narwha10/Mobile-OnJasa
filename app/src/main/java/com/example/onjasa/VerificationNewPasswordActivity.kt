package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class VerificationNewPasswordActivity : AppCompatActivity() {

    private lateinit var verificationTextView: TextView
    private lateinit var clickEmailTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var resendTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var auth: FirebaseAuth
    private var isVerificationLinkClicked = false
    private val timerDuration: Long = 5 * 60 * 1000 // 5 menit dalam milidetik
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_new_password)

        // Inisialisasi komponen UI
        verificationTextView = findViewById(R.id.Verification)
        clickEmailTextView = findViewById(R.id.clickEmail)
        imageView = findViewById(R.id.gambarLoading)
        resendTextView = findViewById(R.id.kirimUlang)
        timerTextView = findViewById(R.id.Timer)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Jalankan timer untuk mengatur tombol "Kirim Ulang"
        startTimer()

        // Tombol "Kirim Ulang" email verifikasi
        resendTextView.setOnClickListener {
            if (timer == null) {
                resendVerificationEmail()
                startTimer()
            } else {
                Toast.makeText(
                    this,
                    "Silakan tunggu hingga timer selesai untuk mengirim ulang email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Jalankan pengecekan status verifikasi email
        checkEmailVerification()
    }

    private fun startTimer() {
        resendTextView.isEnabled = false
        timer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                resendTextView.isEnabled = true
                timer = null
                timerTextView.text = "00:00"
            }
        }.start()
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser
        user?.let {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Email verifikasi telah dikirim ulang.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Gagal mengirim ulang email: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun checkEmailVerification() {
        // Jalankan pengecekan verifikasi setiap 5 detik
        val thread = Thread {
            while (!isVerificationLinkClicked) {
                auth.currentUser?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful && auth.currentUser?.isEmailVerified == true) {
                        isVerificationLinkClicked = true
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Email berhasil diverifikasi!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToNewPassword()
                        }
                    }
                }
                Thread.sleep(5000) // Periksa setiap 5 detik
            }
        }
        thread.start()
    }

    private fun navigateToNewPassword() {
        val intent = Intent(this, NewPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }
}