package com.github.michaelbull.recaptcha.model

/**
 * [Site Verify Request](https://developers.google.com/recaptcha/docs/verify#api_request)
 */
data class SiteVerifyRequest(
    val action: String,
    val response: String,
    val remoteIp: String? = null
)
