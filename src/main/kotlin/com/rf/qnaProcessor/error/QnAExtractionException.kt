package com.rf.qnaProcessor.error

open class QnAExtractionException(message:String): Exception(message)

class BlockedQnAExtractionException(message:String): QnAExtractionException(message)

class ProductNotFoundExtractionException(message:String): Exception(message)