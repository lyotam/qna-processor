package com.rf.qnaProcessor.model

data class QnAEntry (
    val qId: String,
    val votes: Int?,
    val question: String,
    val answer: String?
)