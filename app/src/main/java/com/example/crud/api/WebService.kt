package com.example.crud.api

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object AppConstantes {
    const val BASE_URL = "http://192.168.1.11:3000/"
}

interface WebService {

    @GET("/usuarios")
    suspend fun obtenerUsuarios(): Response<UsuariosResponse>

    @GET("/usuario/{idUsuario}")
    suspend fun obtenerUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<Usuario>

    @POST("/usuario/add")
    suspend fun agregarUsuario(
        @Body usuario: Usuario
    ): Response<String>

    @PUT("/usuario/update/{idUsuario}")
    suspend fun actualizarUsuario(
        @Path("idUsuario") idUsuario: Int,
        @Body usuario: Usuario
    ): Response<String>

    @DELETE("/usuario/delete/{idUsuario}")
    suspend fun borrarUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<String>
}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}