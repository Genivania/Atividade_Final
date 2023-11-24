package br.senai.sp.jandira.atividade_final

import com.google.gson.annotations.SerializedName

data class BaseResponse<T> (
    @SerializedName("data")
    var data: T? = null
)