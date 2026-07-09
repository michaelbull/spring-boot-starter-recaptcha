package com.github.michaelbull.recaptcha.configuration

import com.github.michaelbull.recaptcha.service.RecaptchaFormValidator
import com.github.michaelbull.recaptcha.service.RecaptchaValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner

class RecaptchaAutoConfigurationTest {

    private val runner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RecaptchaAutoConfiguration::class.java))
        .withPropertyValues(
            "recaptcha.keys.site=exampleSite",
            "recaptcha.keys.secret=exampleSecret",
        )

    @Test
    fun createsFormValidatorWithInjectedRequest() {
        runner.run { context ->
            assertThat(context).hasSingleBean(RecaptchaFormValidator::class.java)
        }
    }

    @Test
    fun createsValidator() {
        runner.run { context ->
            assertThat(context).hasSingleBean(RecaptchaValidator::class.java)
        }
    }
}
