package com.github.michaelbull.recaptcha.policy

import com.github.michaelbull.recaptcha.model.SiteVerifyExchange

/**
 * A [RecaptchaPolicy] that rejects a verified exchange whose score is below [minScore] with the `captcha.error.score`
 * code. A `null` score (never expected on a successful v3 verification) is accepted.
 */
class ScoreThresholdPolicy(
    private val minScore: Double
) : RecaptchaPolicy {

    override fun evaluate(exchange: SiteVerifyExchange): RecaptchaDecision {
        val score = exchange.response.score

        return if (score != null && score < minScore) {
            RecaptchaDecision.Reject("captcha.error.score")
        } else {
            RecaptchaDecision.Accept
        }
    }
}
