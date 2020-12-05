package com.example.foodvenueapp.domain.model

const val UNKNOWN_ERROR_CODE = -1
const val BAD_REQUEST_ERROR_CODE = 400

sealed class ResultWrapper<out R> {
    data class Success<out D>(val data: D?) : ResultWrapper<D>()
    data class Error(val code: Int = UNKNOWN_ERROR_CODE) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()
}