package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.configuration.RecaptchaProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.validation.MapBindingResult
import org.springframework.web.client.RestClient

class RecaptchaFormValidatorTest {

    private val properties = RecaptchaProperties(
        url = "http://example.com",
        keys = RecaptchaProperties.Keys(
            site = "exampleSite",
            secret = "exampleSecret",
        ),
    )

    private val validator = RecaptchaFormValidator(
        RecaptchaValidator(RecaptchaVerifier(RestClient.create(), properties)),
        MockHttpServletRequest(),
    )

    @Test
    fun supportsRecaptchaForm() {
        assertTrue(validator.supports(TestForm::class.java))
    }

    @Test
    fun doesNotSupportOtherType() {
        assertFalse(validator.supports(Any::class.java))
    }

    @Test
    fun rejectsResponseTokenFieldOnError() {
        val form = TestForm(recaptchaAction = null, recaptchaResponseToken = "myInput")
        val errors = MapBindingResult(HashMap<String, Any>(), "form")

        validator.validate(form, errors)

        assertEquals("Captcha action missing.", errors.getFieldError("recaptchaResponseToken")?.defaultMessage)
    }

    private class TestForm(
        override val recaptchaAction: String?,
        override val recaptchaResponseToken: String?,
    ) : RecaptchaForm
}
