package com.github.michaelbull.recaptcha.model

data class SiteVerifyExchange(
    val request: SiteVerifyRequest,
    val response: SiteVerifyResponse
)
