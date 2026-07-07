package com.github.michaelbull.recaptcha.i18n

import org.springframework.context.support.ResourceBundleMessageSource

/**
 * A [ResourceBundleMessageSource] backed by the starter's bundled `captcha.error.*` default messages, mirroring the
 * approach Spring Security takes with its `SpringSecurityMessageSource`. Consumers override the wording (or add
 * translations) by defining the same keys in their own application message bundle.
 */
class RecaptchaMessageSource : ResourceBundleMessageSource() {
    init {
        setBasename("com.github.michaelbull.recaptcha.messages")
        setDefaultEncoding("UTF-8")
    }
}
