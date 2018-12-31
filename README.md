# Offer System

A simple RESTful to make offers

It's a system which is possible adding new offers, list and cancel.

## How do I build?
``` Shell
mvn clean install
```

## How do I run it?
``` Shell
java -jar offer-0.0.1-SNAPSHOT.jar
```
Accessing the root you will be redirected to the swagger.

http://localhost:8080

## Endpoints available
* List valid offers: ``` GET /api/offers ```
* Save offers: ``` POST /api/offers ```
* Cancel offers: ``` PUT /api/offers/{id} ```
* List all offers, including the expired/canceled ones: ``` GET /api/offers/all ```

For further details about the request, see wiki.
https://github.com/jcervelin/offer/wiki/Offers

## Tests
``` Shell
mvn test
```
Unit tests and integrated tests using mockito, mock mvc, junit and % of test coverage verified by jacoco.
