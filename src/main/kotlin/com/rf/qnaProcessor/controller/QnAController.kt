package com.rf.qnaProcessor.controller

import com.rf.qnaProcessor.model.QnAEntry
import com.rf.qnaProcessor.service.QnAService
import com.rf.qnaProcessor.util.Logging
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class QnAController (
    private val qnaService: QnAService
) : Logging {

    @ApiOperation(
        value = "Retrieves Amazon Product Q&A list by ASIN",
        notes = "To specify the desired amount of Q&As, include the amount parameter"
    )
    @ApiResponses(
        ApiResponse(code = 200, message = "The request was processed successfully and the Q&A entry list is returned"),
        ApiResponse(code = 404, message = "The product corresponding with supplied ASIN was not found"),
        ApiResponse(code = 503, message = "An Error occurred while processing the Q&A request")
    )
    @GetMapping("/qna/{asin}")
    @ResponseStatus(HttpStatus.OK)
    fun getProductQnAs(@PathVariable asin: String, @RequestParam(required = false) amount: Int?): List<QnAEntry> {
        log.info { "new product Q&As request - ASIN: $asin, amount: ${amount ?: "Not Specified"}" }
        return qnaService.extractMultipleQnAs(asin, amount)
    }
}
