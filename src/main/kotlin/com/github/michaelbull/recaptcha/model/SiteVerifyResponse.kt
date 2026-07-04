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

    @JsonProperty("success")
    val success: Boolean? = null,

    @JsonProperty("score")
    val score: Double? = null,

    @JsonProperty("action")
    val action: String? = null,

    @JsonProperty("challenge_ts")
    val challengeTimestamp: String? = null,

    @JsonProperty("hostname")
    val hostname: String? = null,

    @JsonProperty("error-codes")
    val errorCodes: List<String>? = null
)
