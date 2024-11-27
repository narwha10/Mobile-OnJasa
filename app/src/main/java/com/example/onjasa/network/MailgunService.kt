package com.example.onjasa.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface MailgunService {
    @FormUrlEncoded
    @POST("v3/https://app.mailgun.com/app/sending/domains/sandbox904e79d7c17043cdbf0714ff4cc15f9c.mailgun.org/messages")
    fun sendEmail(
        @Header("Authorization") authorization: String,
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("subject") subject: String,
        @Field("text") text: String
    ): Call<Void>
}