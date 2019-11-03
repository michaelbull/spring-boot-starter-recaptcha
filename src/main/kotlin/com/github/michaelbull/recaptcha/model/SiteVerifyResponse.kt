package com.github.michaelbull.recaptcha.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * [Site Verify Response](https://developers.google.com/recaptcha/docs/v3#site-verify-response)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(
    "success",
    "score",
    "action",
    "challenge_ts",
    "hostname",
    "error-codes"
)
data class SiteVerifyResponse(
    @get:JsonProperty("success")
    val success: Boolean? = null,

    @get:JsonProperty("score")
    val score: Double? = null,

    @get:JsonProperty("action")
    val action: String? = null,

    @get:JsonProperty("challenge_ts")
    val challengeTimestamp: String? = null,

    @get:JsonProperty("hostname")
    val hostname: String? = null,

    @get:JsonProperty("error-codes")
    val errorCodes: List<String>? = null
)
