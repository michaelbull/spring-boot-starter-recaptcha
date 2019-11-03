package com.github.michaelbull.recaptcha.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.recaptcha.configuration.RecaptchaProperties
import com.github.michaelbull.recaptcha.model.SiteVerifyError
import com.github.michaelbull.recaptcha.model.SiteVerifyResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.DefaultResponseCreator
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

class RecaptchaVerifierTest {

    private val properties = RecaptchaProperties(
        url = "http://example.com",
        parameters = RecaptchaProperties.Parameters(
            action = "actionParam",
            responseToken = "responseParam"
        ),
        keys = RecaptchaProperties.Keys(
            site = "exampleSite",
            secret = "exampleSecret"
        )
    )

    private val rest = RestTemplate()
    private val server = MockRestServiceServer.createServer(rest)
    private val testee = RecaptchaVerifier(rest, properties.url, properties.keys.secret)
    private val objectMapper = ObjectMapper()
    private val request = requestTo("http://example.com?secret=exampleSecret&response=myInput&remoteip=myIp")

    @Test
    fun validResponse() {
        val response = SiteVerifyResponse(
            success = true,
            action = "myAction",
            errorCodes = emptyList()
        )

        server.expect(request).andRespond(withVerifyResponse(response))

        val result = testee.verify("myIp", "myAction", "myInput")
        assertEquals(response, result.unwrap().response)
    }

    @Test
    fun invalidResponse() {
        val errorCodes = listOf("invalid-input-response")
        val response = SiteVerifyResponse(
            success = false,
            action = "myAction",
            errorCodes = errorCodes
        )

        server.expect(request).andRespond(withVerifyResponse(response))

        val result = testee.verify("myIp", "myAction", "myInput")
        assertEquals(Err(SiteVerifyError.Response(errorCodes)), result)
    }

    @Test
    fun malformedResponse() {
        server.expect(request).andRespond(withJson("malformed json"))

        val result = testee.verify("myIp", "myAction", "myInput")
        val error = (result as Err).error as SiteVerifyError.Request
        assertThat(error.throwable, instanceOf(RestClientException::class.java))
    }

    private fun withVerifyResponse(response: SiteVerifyResponse): ResponseCreator {
        return withJson(objectMapper.writeValueAsString(response))
    }

    private fun withJson(json: String): DefaultResponseCreator {
        return withSuccess(json, MediaType.APPLICATION_JSON)
    }
}
