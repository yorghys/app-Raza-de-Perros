package com.example.retrofitexampleapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

//La interface principal que usa Retrofit para hacer sus llamadas
//En este caso es un GET pero puede ser un post y demas
//Usamos el URL  para indicar una modificación en la misma, pueden ser Query o Path

interface ApiService {
    @GET
    fun getCharacterByName(@Url url:String): Call<DogsResponse>
}