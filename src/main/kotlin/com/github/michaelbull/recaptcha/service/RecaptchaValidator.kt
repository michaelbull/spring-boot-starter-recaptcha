package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.i18n.RecaptchaMessageSource
import com.github.michaelbull.recaptcha.model.SiteVerifyError
import com.github.michaelbull.recaptcha.model.SiteVerifyResult
import com.github.michaelbull.result.onErr
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import jakarta.servlet.http.HttpServletRequest

class RecaptchaValidator(
    private val recaptchaVerifier: RecaptchaVerifier,
    private val messageSource: MessageSource = RecaptchaMessageSource()
) {

    fun validate(
        field: String,
        request: HttpServletRequest,
        action: String?,
        responseToken: String?,
        errors: Errors
    ): SiteVerifyResult {
        return recaptchaVerifier
            .verify(request.ipAddress, action, responseToken)
            .onErr { error ->
                val code = error.toErrorCode()
                val defaultMessage = messageSource.getMessage(code, null, LocaleContextHolder.getLocale())
                errors.rejectValue(field, code, defaultMessage)
            }
    }

    private val HttpServletRequest.ipAddress: String
        get() = getHeader("X-Forwarded-For")?.split(",")?.firstOrNull() ?: remoteAddr

    private fun SiteVerifyError.toErrorCode(): String = when (this) {
        is SiteVerifyError.MissingAction -> "captcha.error.actionMissing"
        is SiteVerifyError.Incomplete -> "captcha.error.incomplete"
        is SiteVerifyError.Request -> "captcha.error.request"
        is SiteVerifyError.MissingResponseBody -> "captcha.error.responseMissing"
        is SiteVerifyError.Response -> "captcha.error.response"
        is SiteVerifyError.Failed -> "captcha.error.failed"
        is SiteVerifyError.ActionMismatch -> "captcha.error.actionMismatch"
    }
}
