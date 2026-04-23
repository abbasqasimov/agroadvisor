package com.ram.agroadvisor.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AgroRetrofitInstance {
    val api: AgroApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://agroadvisor-api-14f883.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AgroApi::class.java)
    }
}