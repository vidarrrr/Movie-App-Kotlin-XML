package com.movie.app.data.source.remote

import com.movie.app.data.service.MovieService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val API_URL = "https://my-json-server.typicode.com/vidarrrr/fake-movies-api/"

    private val retrofit by lazy{
        Retrofit.Builder().baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MovieService by lazy{
        retrofit.create(MovieService::class.java)
    }
}