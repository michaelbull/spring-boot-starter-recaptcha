package com.github.michaelbull.recaptcha.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * A [Validator] that validates any [RecaptchaForm] through [RecaptchaValidator], rejecting the
 * `recaptchaResponseToken` field on failure.
 *
 * Registered automatically; add it to your `WebDataBinder` (for example from an `@InitBinder` method) alongside your
 * own validators. Supply your own [Validator] bean instead if you need different behaviour.
 */
class RecaptchaFormValidator(
    private val recaptchaValidator: RecaptchaValidator,
    private val request: HttpServletRequest,
) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return RecaptchaForm::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val form = target as RecaptchaForm

        recaptchaValidator.validate(
            field = "recaptchaResponseToken",
            request = request,
            action = form.recaptchaAction,
            responseToken = form.recaptchaResponseToken,
            errors = errors,
        )
    }
}
