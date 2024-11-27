package com.example.onjasa.network

import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MailgunClient {
    private val apiKey = "c02fd0ba-985670b8" // Ganti dengan API Key Anda
    private val sandboxDomain = "https://app.mailgun.com/app/sending/domains/sandbox904e79d7c17043cdbf0714ff4cc15f9c.mailgun.org" // Ganti dengan domain Mailgun Anda

    val service: MailgunService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mailgun.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MailgunService::class.java)
    }

    fun getAuthorizationHeader(): String {
        return Credentials.basic("api", apiKey)
    }

    fun getSandboxDomain(): String {
        return sandboxDomain
    }
}
