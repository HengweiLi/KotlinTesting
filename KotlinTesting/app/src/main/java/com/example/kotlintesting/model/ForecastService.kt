package com.example.kotlintesting.model

import com.example.kotlintesting.model.JsonRootBean
import retrofit2.Call
import retrofit2.http.GET

interface ForecastService {


    @GET("forecast?q=M%C3%BCnchen,DE&appid=b6907d289e10d714a6e88b30761fae22")
    fun getInfo():Call<JsonRootBean>

}