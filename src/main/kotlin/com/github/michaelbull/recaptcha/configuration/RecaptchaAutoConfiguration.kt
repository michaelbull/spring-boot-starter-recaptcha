package com.github.michaelbull.recaptcha.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * [Auto-configuration][EnableAutoConfiguration] for Google reCAPTCHA v3.
 */
@Configuration
@ComponentScan("com.github.michaelbull.recaptcha")
@EnableConfigurationProperties(RecaptchaProperties::class)
class RecaptchaAutoConfiguration @Autowired constructor(
    private val properties: RecaptchaProperties
) {

    @Bean
    fun recaptchaUrl(): String {
        return properties.url
    }

    @Bean
    fun recaptchaSiteKey(): String {
        return properties.keys.site
    }

    @Bean
    fun recaptchaSecretKey(): String {
        return properties.keys.secret
    }
}
