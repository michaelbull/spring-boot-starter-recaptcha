# Spring Boot reCAPTCHA v3 Starter

[![Maven Central](https://img.shields.io/maven-central/v/com.michael-bull.spring-boot-starter-recaptcha/spring-boot-starter-recaptcha.svg)](https://search.maven.org/search?q=g:com.michael-bull.spring-boot-starter-recaptcha)
[![CI](https://github.com/michaelbull/spring-boot-starter-recaptcha/actions/workflows/ci.yaml/badge.svg)](https://github.com/michaelbull/spring-boot-starter-recaptcha/actions/workflows/ci.yaml)
[![License](https://img.shields.io/github/license/michaelbull/spring-boot-starter-recaptcha.svg)](https://github.com/michaelbull/spring-boot-starter-recaptcha/blob/master/LICENSE)

Spring Boot starter for Google's [reCAPTCHA v3][recaptcha-v3].

## Requirements

- Java 17 or later
- Spring Boot 4

## Installation

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.michael-bull.spring-boot-starter-recaptcha:spring-boot-starter-recaptcha:1.0.4")
}
```

## Getting Started

#### 1. Register reCAPTCHA v3 keys

Register your application on the [key registration page][recaptcha-v3-keys].

#### 2. Add the configuration properties to your `application.yaml`:

```yaml
recaptcha.keys:
  site: "<your site key>"
  secret: "<your secret key>"
```

#### 3. Model the form that recaptcha exists on:

```kotlin
class RegisterForm {

    var recaptchaAction: String? = "register"

    var recaptchaResponseToken: String? = null

    @Email
    var email: String? = null
}
```

#### 4. Add a validator for your form:

```kotlin
@Component
@RequestScope
class RegisterFormValidator @Inject constructor(
    private val request: HttpServletRequest,
    private val recaptchaValidator: RecaptchaValidator
) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return RegisterForm::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val form = target as RegisterForm

        recaptchaValidator.validate(
            "recaptchaResponseToken",
            request,
            form.recaptchaAction,
            form.recaptchaResponseToken,
            errors
        )
    }
}
```

#### 5. Bind the validator in your `Controller`:

```kotlin
@Controller
class RegisterController @Inject constructor(
    private val formValidator: RegisterFormValidator
) {

    @InitBinder("form")
    fun initFormBinder(binder: WebDataBinder) {
        binder.addValidators(formValidator)
    }

    /* get and post handlers... */
}
```

## Score

reCAPTCHA v3 scores each interaction from 0.0 (likely a bot) to 1.0 (likely a good interaction). The starter rejects
interactions scoring below `recaptcha.score-threshold`, which defaults to `0.5`, the starting threshold
[Google recommends](https://developers.google.com/recaptcha/docs/v3#interpreting_the_score) ("By default, you can
use a threshold of 0.5"):

```yaml
recaptcha:
  score-threshold: 0.7
```

To decide acceptance using more than the score (for example the client IP address, hostname, or action), provide
your own `RecaptchaPolicy` bean. The starter backs off its default `ScoreThresholdPolicy` when one is present:

```kotlin
@Component
class IpAwareRecaptchaPolicy : RecaptchaPolicy {

    override fun evaluate(exchange: SiteVerifyExchange): RecaptchaDecision {
        val score = exchange.response.score
        val ip = exchange.request.remoteIp

        return if (score != null && score >= 0.5 && ip !in blockedIps) {
            RecaptchaDecision.Accept
        } else {
            RecaptchaDecision.Reject("captcha.error.failed")
        }
    }
}
```

`RecaptchaDecision.Reject` takes an error code. If it names one of the bundled `captcha.error.*` keys the default
message is used; otherwise your application's own `messages.properties` resolves it.

## I18n

The starter bundles English default messages for the `captcha.error.*` codes, so form errors render out of the box
with no configuration. To override the wording or provide translations, define the same keys in your application's
`messages.properties` (your entries take precedence over the bundled defaults):

```properties
captcha.error.actionMissing=Captcha action missing.
captcha.error.incomplete=Captcha incomplete.
captcha.error.request=Failed to submit captcha.
captcha.error.responseMissing=No response from captcha service.
captcha.error.response=Error response from captcha service.
captcha.error.failed=Captcha failed. Please try again.
captcha.error.actionMismatch=Captcha action mismatch.
captcha.error.score=Captcha score too low.
```

## Contributing

Bug reports and pull requests are welcome on [GitHub][github].

## License

This project is available under the terms of the ISC license. See the [`LICENSE`](LICENSE) file for the copyright
information and licensing terms.

[//]: # (@formatter:off)
[recaptcha-v3]: https://developers.google.com/recaptcha/docs/v3
[recaptcha-v3-keys]: https://www.google.com/recaptcha/admin/create
[github]: https://github.com/michaelbull/spring-boot-starter-recaptcha
[//]: # (@formatter:on)
