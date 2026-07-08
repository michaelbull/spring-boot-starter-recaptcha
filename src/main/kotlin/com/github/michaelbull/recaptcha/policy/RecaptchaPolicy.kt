package com.github.michaelbull.recaptcha.policy

import com.github.michaelbull.recaptcha.model.SiteVerifyExchange

/**
 * Decides whether a successfully verified reCAPTCHA [exchange][SiteVerifyExchange] should be accepted, given its
 * score and any other factors it exposes: the client IP via [SiteVerifyExchange.request], and the hostname and
 * action via [SiteVerifyExchange.response].
 *
 * The starter registers a [ScoreThresholdPolicy] by default. Supply your own `@Bean` implementing this interface to
 * replace it (for example to combine the score with the client IP address); the starter backs off via
 * `@ConditionalOnMissingBean`.
 */
interface RecaptchaPolicy {
    fun evaluate(exchange: SiteVerifyExchange): RecaptchaDecision
}
