package com.github.michaelbull.recaptcha.policy

/**
 * The outcome of evaluating a [RecaptchaPolicy] against a successfully verified reCAPTCHA exchange.
 */
sealed interface RecaptchaDecision {

    /**
     * The exchange is acceptable; no error is raised.
     */
    data object Accept : RecaptchaDecision

    /**
     * The exchange is rejected. [errorCode] is passed to `Errors.rejectValue`: if it names one of the starter's
     * bundled `captcha.error.*` keys its default message is used, otherwise the consuming application's own
     * `MessageSource` resolves it.
     */
    data class Reject(val errorCode: String) : RecaptchaDecision
}
