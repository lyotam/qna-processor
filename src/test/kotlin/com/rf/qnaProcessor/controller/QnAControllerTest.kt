package com.rf.qnaProcessor.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.rf.qnaProcessor.error.BlockedQnAExtractionException
import com.rf.qnaProcessor.error.ProductNotFoundExtractionException
import com.rf.qnaProcessor.error.QnAExtractionException
import com.rf.qnaProcessor.generateValidQnAList
import com.rf.qnaProcessor.model.QnAEntry
import com.rf.qnaProcessor.service.QnAService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(QnAController::class)
internal class QnAControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var qnaService: QnAService

    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule())
    }

    @TestConfiguration
    class QnAControllerTestConfig {
        @Bean
        fun qnaService() = mockk<QnAService>()
    }

    @Test
    fun `Given valid ASIN request, a QnAEntry list is returned`() {
        val expected = generateValidQnAList()

        every {
            qnaService.extractMultipleQnAs(any(), any())
        } returns expected

        val res = executeGetRequest(mockMvc, "/qna/B07FZ8S74R?amount=2", MockMvcResultMatchers.status().isOk)
        assertNotNull(res)
        assertEquals(expected, objectMapper.readValue<List<QnAEntry>>(res))
    }

    @Test
    fun `Given wrong ASIN request, when QnAExtractionException is thrown by qnaService, a NotFound response is returned`() {
        every {
            qnaService.extractMultipleQnAs(any(), any())
        } throws ProductNotFoundExtractionException("Product not found")

        executeGetRequest(mockMvc, "/qna/B07FZ8S777", MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `Given request, when BlockedQnAExtractionException is thrown by qnaService, a ServiceUnavailable response is returned`() {
        every {
            qnaService.extractMultipleQnAs(any(), any())
        } throws BlockedQnAExtractionException("Extraction blocked by Amazon")

        executeGetRequest(mockMvc, "/qna/B07FZ8S777", MockMvcResultMatchers.status().isServiceUnavailable)
    }

    @Test
    fun `Given request, when QnAExtractionException is thrown by qnaService, a ServiceUnavailable response is returned`() {
        every {
            qnaService.extractMultipleQnAs(any(), any())
        } throws QnAExtractionException("Extraction failed")

        executeGetRequest(mockMvc, "/qna/B07FZ8S777", MockMvcResultMatchers.status().isServiceUnavailable)
    }

    private fun executeGetRequest(mockMvc: MockMvc, resource: String, responseStatus: ResultMatcher): String =
        mockMvc.perform(
            MockMvcRequestBuilders.get(resource)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(responseStatus)
            .andReturn()
            .response.contentAsString
}