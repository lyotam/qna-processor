package com.rf.qnaProcessor.util

import mu.KLogger
import mu.KotlinLogging

interface Logging {
    val log: KLogger
        get() = KotlinLogging.logger {}
}