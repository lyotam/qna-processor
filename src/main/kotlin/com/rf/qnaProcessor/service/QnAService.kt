package com.rf.qnaProcessor.service

import com.rf.qnaProcessor.error.BlockedQnAExtractionException
import com.rf.qnaProcessor.model.QnAEntry
import com.rf.qnaProcessor.util.Logging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class QnAService (
    val defaultEntryAmount: Int = 10
) : Logging {
    //todo: add error handling
    fun extractMultipleQnA(asin: String, amount: Int?): List<QnAEntry> {
        val qnaEntries = mutableListOf<QnAEntry>()
        var pageNum = 1
        var page = loadPage(asin, pageNum)

        val paginationHeader = page.select("div[class~=a-section askPaginationHeaderMessage] > span").first().text()
        val (totalQuestionsStr) = "of (\\d+)\\+? questions".toRegex().find(paginationHeader)!!.destructured //todo: check null
        val totalQuestionsToExtract = amount?.let { totalQuestionsStr.toInt().coerceAtMost(it) } ?: defaultEntryAmount

        while (qnaEntries.size < totalQuestionsToExtract) {
            extractPageQnAs(page, qnaEntries, totalQuestionsToExtract)
            log.info { "Extracted ASIN $asin entries from page $pageNum" }
            page = loadPage(asin, ++pageNum)
        }
        log.info { "ASIN $asin Extraction completed" }
        log.debug { "Q&A Entries: $qnaEntries" }
        return qnaEntries
    }

    private fun loadPage(asin: String, pageNum: Int): Document {
        val page = Jsoup.connect("https://www.amazon.com/ask/questions/asin/$asin/$pageNum").get()
        if (page.title() == "Robot Check") {
            log.error { "Extraction blocked by Amazon's Robot Check" }
            throw BlockedQnAExtractionException("Service currently blocked, please try again later")
        }
        return page
    }

    private fun extractPageQnAs(page: Document, qnaEntries: MutableList<QnAEntry>, totalToExtract: Int) { //todo: change refence to list
        val qnaElements = page.select("div[class~=a-section askTeaserQuestions]").first().children()
        for (qnaSection in qnaElements) {
            val votes = qnaSection.select("span[class~=count]").first().text()?.toInt()
            val qSection = qnaSection.select("div[id~=^question-]").first()
            val (qId) = "^question-(\\w+)$".toRegex().find(qSection.id())!!.destructured
            val question = qSection.select("span[class~=a-declarative]").first().text()
            val answer = qSection.siblingElements().first()
                .select("div[class~=a-fixed-left-grid-col a-col-right] > span").first()
                ?.run {
                    if (childNodeSize() > 1) select("span[class~=askLongText]").text().removeSuffix(" see less")
                    else text()
                }

            qnaEntries.add(QnAEntry(qId, votes, question, answer))
            if (qnaEntries.size == totalToExtract) break
        }
    }

    //todo: add
    fun extractFullQnA(qId: String) {
        throw NotImplementedError()
    }
}