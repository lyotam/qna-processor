# Q&A-Processor: A Web Server Processor App for Amazon Product Q&A 

Q&A-Processor is a Kotlin based web server app which, upon client request, extracts and processes Amazon product's Questions and Answers. 


## Instructions

In order to receive a list of Q&As for a specific Amazon product, you will need to have the product's ASIN.
You have the option of specifying the amount of Q&As you wish to retrieve (capped to Amazon's 1000 max questions, defaults to 10 if not specified).

You can send the request to the server via the following methods:

#### REST (POSTMAN, CURL)
http://127.0.0.1:8080/qna/{PRODUCT_ASIN}?amount={OPTIONAL_NUMBER}

examples:
- http://127.0.0.1:8080/qna/B07FZ8S74R?amount=25
- http://127.0.0.1:8080/qna/Tx1HMRPBQIOKRYD


#### SWAGGER UI
[Swagger UI](http://127.0.0.1:8080/swagger-ui.html) is also available to interact with the app


## How to run this app:

#### Using Docker
A Docker Image of the App is available in docker hub:
```
docker run -p 8080:8080 lyotam/qna-processor
```

#### Using Running Scripts

First clone the repository and access the directory:
```
$ git clone https://github.com/lyotam/qna-processor.git
$ cd qna-processor
```

Running the app:
```
# To run the app
$ ./start.sh
# To shut down the app
$ ./stop.sh
```