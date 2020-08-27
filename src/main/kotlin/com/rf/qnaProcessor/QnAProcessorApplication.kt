package com.rf.qnaProcessor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QnAProcessorApplication

fun main(args: Array<String>) {
    runApplication<QnAProcessorApplication>(*args)
}