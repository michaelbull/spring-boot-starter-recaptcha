package com.github.michaelbull.recaptcha.service

import com.github.michaelbull.recaptcha.model.SiteVerifyError
import com.github.michaelbull.recaptcha.model.SiteVerifyExchange
import com.github.michaelbull.recaptcha.model.SiteVerifyRequest
import com.github.michaelbull.recaptcha.model.SiteVerifyResponse
import com.github.michaelbull.recaptcha.model.SiteVerifyResult
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.runCatching
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@Service
class RecaptchaVerifier @Autowired constructor(
    private val rest: RestTemplate,
    @Qualifier("recaptchaUrl") private val recaptchaUrl: String,
    @Qualifier("recaptchaSecretKey") private val recaptchaSecretKey: String
) {

    fun verify(ip: String, action: String?, responseToken: String?): SiteVerifyResult {
        return createRequest(ip, action, responseToken)
            .andThen(::post)
            .onSuccess(::log)
            .andThen(::checkErrors)
            .andThen(::checkPassed)
            .andThen(::checkActions)
    }

    private fun createRequest(
        ip: String,
        action: String?,
        responseToken: String?
    ): Result<SiteVerifyRequest, SiteVerifyError> {
        return when {
            action.isNullOrBlank() -> Err(SiteVerifyError.MissingAction)
            responseToken.isNullOrBlank() -> Err(SiteVerifyError.Incomplete)

            else -> {
                val request = SiteVerifyRequest(
                    action = action,
                    response = responseToken,
                    remoteIp = ip
                )

                Ok(request)
            }
        }
    }

    private fun SiteVerifyRequest.toUriComponents(): UriComponents {
        return UriComponentsBuilder.fromHttpUrl(recaptchaUrl)
            .queryParam("secret", recaptchaSecretKey)
            .queryParam("response", response)
            .queryParam("remoteip", remoteIp)
            .build()
    }

    private fun post(uriComponents: UriComponents): ResponseEntity<SiteVerifyResponse> {
        return rest.postForEntity(uriComponents.toUri(), uriComponents)
    }

    private fun post(request: SiteVerifyRequest): SiteVerifyResult {
        val uriComponents = request.toUriComponents()

        return runCatching { post(uriComponents) }
            .mapError(SiteVerifyError::Request)
            .andThen(::getResponseBody)
            .map { response -> SiteVerifyExchange(request, response) }
    }

    private fun log(exchange: SiteVerifyExchange) {
        if (logger.isDebugEnabled) {
            logger.debug("{}", exchange)
        }
    }

    private fun getResponseBody(entity: ResponseEntity<SiteVerifyResponse>): Result<SiteVerifyResponse, SiteVerifyError> {
        val body = entity.body

        return if (body != null) {
            Ok(body)
        } else {
            Err(SiteVerifyError.MissingResponseBody)
        }
    }

    private fun checkErrors(exchange: SiteVerifyExchange): SiteVerifyResult {
        val errorCodes = exchange.response.errorCodes

        return if (errorCodes.isNullOrEmpty()) {
            Ok(exchange)
        } else {
            Err(SiteVerifyError.Response(errorCodes))
        }
    }

    private fun checkPassed(exchange: SiteVerifyExchange): SiteVerifyResult {
        return if (exchange.response.success == true) {
            Ok(exchange)
        } else {
            Err(SiteVerifyError.Failed)
        }
    }

    private fun checkActions(exchange: SiteVerifyExchange): SiteVerifyResult {
        val requestAction = exchange.request.action
        val responseAction = exchange.response.action

        return if (responseAction == requestAction) {
            Ok(exchange)
        } else {
            Err(SiteVerifyError.ActionMismatch(requestAction, responseAction))
        }
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(RecaptchaVerifier::class.java)
    }
}
