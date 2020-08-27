package com.rf.qnaProcessor.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [BlockedQnAExtractionException::class, QnAExtractionException::class])
    fun handleExtractionException(ex: QnAExtractionException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = [ProductNotFoundExtractionException::class])
    fun handleProductNotFoundException(ex: ProductNotFoundExtractionException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND);
    }
}