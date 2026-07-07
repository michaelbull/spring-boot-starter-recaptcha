package com.github.michaelbull.recaptcha.configuration

import com.github.michaelbull.recaptcha.service.RecaptchaValidator
import com.github.michaelbull.recaptcha.service.RecaptchaVerifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

/**
 * [Auto-configuration][AutoConfiguration] for Google reCAPTCHA v3.
 */
@AutoConfiguration
@EnableConfigurationProperties(RecaptchaProperties::class)
class RecaptchaAutoConfiguration {

    @Bean
    fun recaptchaVerifier(rest: RestTemplate, properties: RecaptchaProperties): RecaptchaVerifier {
        return RecaptchaVerifier(rest, properties)
    }

    @Bean
    fun recaptchaValidator(verifier: RecaptchaVerifier): RecaptchaValidator {
        return RecaptchaValidator(verifier)
    }
}
