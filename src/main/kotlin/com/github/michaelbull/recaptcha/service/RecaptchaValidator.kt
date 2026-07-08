package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.i18n.RecaptchaMessageSource
import com.github.michaelbull.recaptcha.model.SiteVerifyError
import com.github.michaelbull.recaptcha.model.SiteVerifyResult
import com.github.michaelbull.recaptcha.policy.RecaptchaDecision
import com.github.michaelbull.recaptcha.policy.RecaptchaPolicy
import com.github.michaelbull.recaptcha.policy.ScoreThresholdPolicy
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors

class RecaptchaValidator(
    private val recaptchaVerifier: RecaptchaVerifier,
    private val policy: RecaptchaPolicy = ScoreThresholdPolicy(0.5),
    private val messageSource: MessageSource = RecaptchaMessageSource(),
) {

    fun validate(
        field: String,
        request: HttpServletRequest,
        action: String?,
        responseToken: String?,
        errors: Errors,
    ): SiteVerifyResult {
        return recaptchaVerifier
            .verify(request.ipAddress, action, responseToken)
            .onErr { error -> reject(errors, field, error.toErrorCode()) }
            .onOk { exchange ->
                when (val decision = policy.evaluate(exchange)) {
                    is RecaptchaDecision.Accept -> Unit
                    is RecaptchaDecision.Reject -> reject(errors, field, decision.errorCode)
                }
            }
    }

    private fun reject(errors: Errors, field: String, code: String) {
        val message = messageSource.getMessage(
            /* code = */ code,
            /* args = */ null,
            /* defaultMessage = */ null,
            /* locale = */ LocaleContextHolder.getLocale(),
        )

        if (message != null) {
            errors.rejectValue(field, code, message)
        } else {
            errors.rejectValue(field, code)
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
