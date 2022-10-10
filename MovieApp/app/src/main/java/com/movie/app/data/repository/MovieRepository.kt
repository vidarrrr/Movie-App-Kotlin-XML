package com.movie.app.data.repository

import com.movie.app.data.source.remote.RetrofitHelper
import com.movie.app.model.MovieModel
import retrofit2.Response

class MovieRepository {
    suspend fun getMovies(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getMovies()
    }
    suspend fun getBoxOffice(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getBoxOffice()
    }
    suspend fun getComingSoon(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getComingSoon()
    }

}