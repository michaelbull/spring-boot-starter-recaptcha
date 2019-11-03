package com.github.michaelbull.recaptcha.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@ConstructorBinding
@ConfigurationProperties("recaptcha")
data class RecaptchaProperties(
    @NotBlank val url: String = "https://www.google.com/recaptcha/api/siteverify",
    @Valid val parameters: Parameters = Parameters(),
    @Valid val keys: Keys
) {

    data class Parameters(
        @NotBlank val action: String = "recaptchaAction",
        @NotBlank val responseToken: String = "recaptchaResponseToken"
    )

    data class Keys(
        @NotBlank val site: String,
        @NotBlank val secret: String
    )
}
