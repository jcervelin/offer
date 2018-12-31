# Offer System

A simple RESTful to make offers

It's an system which is possible add new offers, list and cancel.

## How do I build?
mvn clean install

## How do I run it?
java -jar offer-0.0.1-SNAPSHOT.jar

Accessing the root you will be redirected to the swagger.

http://localhost:8080

## Endpoints availables
* List valid offers: GET /api/offers
* Save offers: POST /api/offers
* Cancel offers: PUT /api/offers
* List all offers, including the expired/canceled ones: GET /api/offers/all

For further details about the request and response, see wiki.
https://github.com/jcervelin/offer/wiki/Offers
