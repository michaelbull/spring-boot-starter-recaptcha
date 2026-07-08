package com.github.michaelbull.recaptcha.policy

import com.github.michaelbull.recaptcha.model.SiteVerifyExchange
import com.github.michaelbull.recaptcha.model.SiteVerifyRequest
import com.github.michaelbull.recaptcha.model.SiteVerifyResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScoreThresholdPolicyTest {

    @Test
    fun rejectsScoreBelowThreshold() {
        val exchange = exchange(score = 0.3)
        assertEquals(RecaptchaDecision.Reject("captcha.error.score"), ScoreThresholdPolicy(0.5).evaluate(exchange))
    }

    @Test
    fun acceptsScoreAtThreshold() {
        val exchange = exchange(score = 0.5)
        assertEquals(RecaptchaDecision.Accept, ScoreThresholdPolicy(0.5).evaluate(exchange))
    }

    @Test
    fun acceptsNullScore() {
        val exchange = exchange(score = null)
        assertEquals(RecaptchaDecision.Accept, ScoreThresholdPolicy(0.5).evaluate(exchange))
    }

    private fun exchange(score: Double?): SiteVerifyExchange {
        return SiteVerifyExchange(
            request = SiteVerifyRequest(action = "myAction", response = "myInput", remoteIp = "myIp"),
            response = SiteVerifyResponse(success = true, action = "myAction", score = score)
        )
    }
}
