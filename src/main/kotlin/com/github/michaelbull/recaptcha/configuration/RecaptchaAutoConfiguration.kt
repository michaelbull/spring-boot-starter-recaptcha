package com.github.michaelbull.recaptcha.configuration

import com.github.michaelbull.recaptcha.policy.RecaptchaPolicy
import com.github.michaelbull.recaptcha.policy.ScoreThresholdPolicy
import com.github.michaelbull.recaptcha.service.RecaptchaFormValidator
import com.github.michaelbull.recaptcha.service.RecaptchaValidator
import com.github.michaelbull.recaptcha.service.RecaptchaVerifier
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

/**
 * [Auto-configuration][AutoConfiguration] for Google reCAPTCHA v3.
 */
@AutoConfiguration
@EnableConfigurationProperties(RecaptchaProperties::class)
class RecaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun recaptchaVerifier(builders: ObjectProvider<RestClient.Builder>, properties: RecaptchaProperties): RecaptchaVerifier {
        val rest = builders.getIfAvailable(RestClient::builder).build()
        return RecaptchaVerifier(rest, properties)
    }

    @Bean
    @ConditionalOnMissingBean
    fun recaptchaPolicy(properties: RecaptchaProperties): RecaptchaPolicy {
        return ScoreThresholdPolicy(properties.scoreThreshold)
    }

    @Bean
    @ConditionalOnClass(HttpServletRequest::class)
    @ConditionalOnMissingBean
    fun recaptchaValidator(verifier: RecaptchaVerifier, policy: RecaptchaPolicy): RecaptchaValidator {
        return RecaptchaValidator(verifier, policy)
    }

    @Bean
    @ConditionalOnClass(HttpServletRequest::class)
    @ConditionalOnMissingBean
    fun recaptchaFormValidator(validator: RecaptchaValidator, request: HttpServletRequest): RecaptchaFormValidator {
        return RecaptchaFormValidator(validator, request)
    }
}
