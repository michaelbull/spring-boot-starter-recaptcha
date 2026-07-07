package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.configuration.RecaptchaProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.MapBindingResult
import org.springframework.web.client.RestClient

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
}
