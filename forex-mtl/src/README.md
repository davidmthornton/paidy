# forex-mtl

## A local proxy for Forex rates

### Description

Forex is a simple application that acts as a local proxy for getting exchange rates. The service can be consumed by 
other internal services to get the exchange rate between a set of currencies, so they don't have to care about the 
specifics of third-party providers.

The service:-
- returns a cached exchange rate when provided with 2 [supported currencies](https://github.com/davidmthornton/paidy/blob/6dfdc5aa4bc9afdb2400336af9c5443a988edb42/forex-mtl/src/main/scala/forex/domain/Currency.scala#L7)
- the max age of the cached exchange rate [is configurable](https://github.com/davidmthornton/paidy/blob/6dfdc5aa4bc9afdb2400336af9c5443a988edb42/forex-mtl/src/main/resources/application.conf#L14)
- the service supports at least 10,000 successful requests per day with [1 API token](https://github.com/davidmthornton/paidy/blob/6dfdc5aa4bc9afdb2400336af9c5443a988edb42/forex-mtl/src/main/resources/application.conf#L13)

### How it works

In order to support a minimum of 10,000 requests per day with an API token with a limit of only 1000 requests per day,
this service makes a single request for all currency exchange rates at a configured time interval and caches the result.
This service exposes the cached exchange rate to clients. 

### Endpoints

The service runs on port 8088

#### GET /rates?from=[origin_currency]&to=[target_currency]

|   Name | Required |  Type   | Description                              |
|-------:|:--------:|:-------:|------------------------------------------|
| `from` | required | string  | The origin currency to be exchanged from |
|   `to` | required | string  | The target currency to be exchanged to   |

#### Example response

``` json
{
"from": "GBP",
"to": "JPY",
"price": 0.90894669193547555,
"timestamp": "2023-12-08T09:34:40.197Z"
}
```

#### Improvement ideas

- add validation on the configured ```cache-fetch-interval``` to ensure it is not set out of bounds of the age requirements
- support for multiple rates on a single inbound request
- more tests
  - full test coverage
  - performance tests to ensure the service meets the performance requirements