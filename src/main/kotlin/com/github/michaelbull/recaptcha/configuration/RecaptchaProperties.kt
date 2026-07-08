package com.github.michaelbull.recaptcha.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank

/**
 * [@ConfigurationProperties][ConfigurationProperties] for Google reCAPTCHA v3.
 */
@Validated
@ConfigurationProperties("recaptcha")
data class RecaptchaProperties(

    /**
     * The URL used to [verify the user's response](https://developers.google.com/recaptcha/docs/verify).
     */
    @NotBlank val url: String = "https://www.google.com/recaptcha/api/siteverify",

    /**
     * The minimum [score](https://developers.google.com/recaptcha/docs/v3#interpreting_the_score) an interaction must
     * reach to be accepted by the default score-threshold policy, between 0.0 and 1.0.
     */
    @DecimalMin("0.0") @DecimalMax("1.0") val scoreThreshold: Double = 0.5,

    @Valid val keys: Keys
) {

    data class Keys(

        /**
         * The key in the HTML code your site serves to users.
         */
        @NotBlank val site: String,

        /**
         * The secret key for communication between your site and reCAPTCHA.
         */
        @NotBlank val secret: String
    )
}
