package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.configuration.RecaptchaProperties
import com.github.michaelbull.recaptcha.model.SiteVerifyResponse
import com.github.michaelbull.recaptcha.policy.ScoreThresholdPolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.validation.MapBindingResult
import org.springframework.web.client.RestClient
import tools.jackson.databind.ObjectMapper

class RecaptchaValidatorTest {

    private val properties = RecaptchaProperties(
        url = "http://example.com",
        keys = RecaptchaProperties.Keys(
            site = "exampleSite",
            secret = "exampleSecret"
        )
    )

    @Test
    fun populatesBundledDefaultMessageOnError() {
        val verifier = RecaptchaVerifier(RestClient.create(), properties)
        val validator = RecaptchaValidator(verifier)
        val errors = MapBindingResult(HashMap<String, Any>(), "form")

        validator.validate("recaptchaResponseToken", MockHttpServletRequest(), null, "myInput", errors)

        assertEquals("Captcha action missing.", errors.getFieldError("recaptchaResponseToken")?.defaultMessage)
    }

    @Test
    fun rejectsScoreBelowThreshold() {
        val builder = RestClient.builder()
        val server = MockRestServiceServer.bindTo(builder).build()
        val verifier = RecaptchaVerifier(builder.build(), properties)
        val validator = RecaptchaValidator(verifier, ScoreThresholdPolicy(0.5))
        val errors = MapBindingResult(HashMap<String, Any>(), "form")

        val response = SiteVerifyResponse(success = true, action = "myAction", score = 0.1, errorCodes = emptyList())
        server
            .expect(requestTo("http://example.com?secret=exampleSecret&response=myInput&remoteip=127.0.0.1"))
            .andRespond(withSuccess(ObjectMapper().writeValueAsString(response), MediaType.APPLICATION_JSON))

        validator.validate("recaptchaResponseToken", MockHttpServletRequest(), "myAction", "myInput", errors)

        assertEquals("Captcha score too low.", errors.getFieldError("recaptchaResponseToken")?.defaultMessage)
    }
}
