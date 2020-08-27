package com.rf.qnaProcessor.controller

import com.rf.qnaProcessor.model.QnAEntry
import com.rf.qnaProcessor.service.QnAService
import com.rf.qnaProcessor.util.Logging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class QnAController (
    private val qnaService: QnAService
) : Logging {
    @GetMapping("/qna/{asin}")
    @ResponseStatus(HttpStatus.OK)
    fun getProductQnAs(@PathVariable asin: String, @RequestParam(required = false) amount: Int?): List<QnAEntry> {
        log.info { "new product Q&As request - ASIN: $asin, amount: ${amount ?: "Not Specified"}" }
        return qnaService.extractMultipleQnAs(asin, amount)
    }
}
