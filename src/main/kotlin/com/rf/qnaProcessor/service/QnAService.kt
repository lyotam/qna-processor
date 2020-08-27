package com.rf.qnaProcessor.service

import com.rf.qnaProcessor.error.BlockedQnAExtractionException
import com.rf.qnaProcessor.error.ProductNotFoundExtractionException
import com.rf.qnaProcessor.error.QnAExtractionException
import com.rf.qnaProcessor.model.QnAEntry
import com.rf.qnaProcessor.util.Logging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class QnAService (
    val defaultEntryAmount: Int = 10
) : Logging {
    fun extractMultipleQnA(asin: String, amount: Int?): List<QnAEntry> {
        val qnaEntries = mutableListOf<QnAEntry>()
        var pageNum = 1
        var page = loadPage(asin, pageNum)

        val paginationHeader = page.select("div[class~=a-section askPaginationHeaderMessage] > span").first().text()
        val (totalQuestionsStr) = "of (\\d+)\\+? questions".toRegex().find(paginationHeader)?.destructured
            ?: throw QnAExtractionException("Page format changed")
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
        try {
            val page = Jsoup.connect("https://www.amazon.com/ask/questions/asin/$asin/$pageNum").get()
            if (page.title() == "Robot Check") {
                log.error { "Extraction blocked by Amazon's Robot Check" }
                throw BlockedQnAExtractionException("Service currently blocked, please try again later")
            }
            return page
        } catch (ex: HttpStatusException) {
            if (ex.statusCode == 404) throw ProductNotFoundExtractionException("Product page not found for ASIN $asin")
            else throw QnAExtractionException("Extraction from Amazon cannot be established, try again later")
        }
    }

    //todo: change refence to list
    private fun extractPageQnAs(page: Document, qnaEntries: MutableList<QnAEntry>, totalToExtract: Int) {
        val qnaElements = page.select("div[class~=a-section askTeaserQuestions]").first().children()
        for (qnaSection in qnaElements) {
            val votes = qnaSection.select("span[class~=count]").first().text()?.toInt()
            val qSection = qnaSection.select("div[id~=^question-]").first()
            val (qId) = "^question-(\\w+)$".toRegex().find(qSection.id())?.destructured
                ?: throw QnAExtractionException("Page format changed")
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
}