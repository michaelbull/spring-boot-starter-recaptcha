package com.github.michaelbull.recaptcha.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan("com.github.michaelbull.recaptcha")
@EnableConfigurationProperties(RecaptchaProperties::class)
class RecaptchaConfiguration @Autowired constructor(
    private val properties: RecaptchaProperties
) {

    @Bean
    fun recaptchaUrl(): String {
        return properties.url
    }

    @Bean
    fun recaptchaActionParameter(): String {
        return properties.parameters.action
    }

    @Bean
    fun recaptchaResponseTokenParameter(): String {
        return properties.parameters.responseToken
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
