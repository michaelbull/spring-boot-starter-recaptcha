package com.github.michaelbull.recaptcha.service

/**
 * A form carrying the reCAPTCHA v3 action and response token. Implement this on your form to have the
 * auto-configured [RecaptchaFormValidator] validate it, instead of writing your own
 * [org.springframework.validation.Validator].
 */
interface RecaptchaForm {

    /**
     * The [action](https://developers.google.com/recaptcha/docs/v3#actions) the response token was generated for.
     */
    val recaptchaAction: String?

    /**
     * The response token produced by the reCAPTCHA client and submitted with the form.
     */
    val recaptchaResponseToken: String?
}
