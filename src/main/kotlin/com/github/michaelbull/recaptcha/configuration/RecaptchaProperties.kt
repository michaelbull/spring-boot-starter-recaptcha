package com.github.michaelbull.recaptcha.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank

/**
 * [@ConfigurationProperties][ConfigurationProperties] for Google reCAPTCHA v3.
 */
@Validated
@ConstructorBinding
@ConfigurationProperties("recaptcha")
data class RecaptchaProperties(

    /**
     * The URL used to [verify the user's response](https://developers.google.com/recaptcha/docs/verify).
     */
    @NotBlank val url: String = "https://www.google.com/recaptcha/api/siteverify",

    @Valid val parameters: Parameters = Parameters(),
    @Valid val keys: Keys
) {

    data class Parameters(

        /**
         * The name of the hidden input field which contains the current action.
         */
        @NotBlank val action: String = "recaptchaAction",

        /**
         * The name of the hidden input field that is to be populated with the response token.
         */
        @NotBlank val responseToken: String = "recaptchaResponseToken"
    )

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
