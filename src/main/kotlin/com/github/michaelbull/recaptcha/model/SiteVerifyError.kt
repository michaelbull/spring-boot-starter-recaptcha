package com.github.michaelbull.recaptcha.model

sealed class SiteVerifyError {
    object MissingAction : SiteVerifyError()
    object Incomplete : SiteVerifyError()
    data class Request(val throwable: Throwable) : SiteVerifyError()
    object MissingResponseBody : SiteVerifyError()
    data class Response(val errorCodes: List<String>) : SiteVerifyError()
    object Failed : SiteVerifyError()
    data class ActionMismatch(val requestAction: String, val responseAction: String?) : SiteVerifyError()
}
