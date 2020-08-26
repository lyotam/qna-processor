package com.rf.qnaProcessor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QAProcessorApplication

fun main(args: Array<String>) {
    runApplication<QAProcessorApplication>(*args)
}