package com.rf.qnaProcessor.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [BlockedQnAExtractionException::class])
    fun handleNotFoundException(ex: BlockedQnAExtractionException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}