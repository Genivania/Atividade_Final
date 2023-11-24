package br.senai.sp.jandira.atividade_final

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("/usuario/listar/{NomeUsuario}")
    suspend fun getUsuarioByID(@Path("NomeUsuario") NomeUsuario: String): Response<BaseResponse<CategoryResponse>>

    @POST("/usuario/cadastroUsuario")
    suspend fun createUsuario(@Body body: JsonObject): Response<JsonObject>
}
