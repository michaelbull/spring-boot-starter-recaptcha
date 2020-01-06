package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.model.SiteVerifyError
import com.github.michaelbull.recaptcha.model.SiteVerifyResult
import com.github.michaelbull.result.onFailure
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.validation.Errors
import javax.servlet.http.HttpServletRequest

@Service
class RecaptchaValidator @Autowired constructor(
    private val recaptchaVerifier: RecaptchaVerifier
) {

    fun validate(
        field: String,
        request: HttpServletRequest,
        action: String?,
        responseToken: String?,
        errors: Errors
    ): SiteVerifyResult {
        return recaptchaVerifier.verify(request.ipAddress, action, responseToken).onFailure { error ->
            errors.rejectValue(field, error.toErrorCode())
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
