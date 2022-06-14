### Run the application using below command:

``mvn spring-boot:run``

### Access the API documentation here:
[Api Docs](http://localhost:8080/swagger-ui/index.html#)
(App should be running locally to acces this)
### Considerations for future increments 

- Strict Validations & Authorizations on Request Payloads
- Concurrency Tests and further improvements
- Though the file processing is done asynchronously, have to improve if the file sizes are huge
- Metrics implementation for Monitoring
- Proper functional responses for different error situations and also success cases
- improve swagger docs
- Pagination for getProducts and getArticles

The focus was mainly getting the core functionality ready given the timelines and it is a simple application that can be deployed to Prod. But If we had more time the above improvements will make the app more adaptable for Prodcution.

### Endpoint Details:

#### GET /articles/load
Loads the articles into the system from the supplied file.
- Returns 202 Accepted if the file is present and processes it asynchronously
- Returns 400 Bad Request if the file is not present
```
curl -i -X 'GET' 'http://localhost:8080/articles/load?filePath=src/main/resources/inventory.json'
```
#### GET /articles
Gets all the articles from the system.
- Returns 200
```
curl -i -X 'GET' 'http://localhost:8080/articles'
```
```
[
    {
        "name": "leg",
        "stock": 12,
        "art_id": 1
    },
    {
        "name": "screw",
        "stock": 17,
        "art_id": 2
    },
    {
        "name": "seat",
        "stock": 2,
        "art_id": 3
    },
    {
        "name": "table top",
        "stock": 1,
        "art_id": 4
    }
]
```
#### GET /articles/{id}
Gets the article by id
- Returns 200
- Returns 404 if the article is not present
```
curl -i -X 'GET' 'http://localhost:8080/articles/1'
```

```
{
    "name": "leg",
    "stock": 12,
    "art_id": 1
}
```
#### POST /articles
Creates an article
- Returns 201 Created
- Returns 400 Bad Request
```
curl -i -X 'POST' 'http://localhost:8080/articles' -H "Content-Type: application/json"  -d '{"name": "leg","stock":12}'
```
#### PUT /articles/{id}
Updates an article
- Returns 204 No Content
- Returns 400 Bad Request
```
curl -i -X 'PUT' 'http://localhost:8080/articles/1' -H "Content-Type: application/json"  -d '{"name": "leg","stock":12}'
```

#### GET /products/load
Loads the products into the system from the supplied file.
- Returns 202 Accepted if the file is present and processes it asynchronously
- Returns 400 Bad Request if the file is not present
```
curl -i -X 'GET' 'http://localhost:8080/products/load?filePath=src/main/resources/products.json'
```
#### GET /products
Gets all the products from the system.
- Returns 200
```
curl -i -X 'GET' 'http://localhost:8080/products'
```
```
{
    "products": [
        {
            "id": 1,
            "name": "Dining Chair",
            "available": true,
            "stock": 2,
            "contain_articles": [
                {
                    "art_id": 1,
                    "amount_of": 4
                },
                {
                    "art_id": 2,
                    "amount_of": 8
                },
                {
                    "art_id": 3,
                    "amount_of": 1
                }
            ]
        },
        {
            "id": 2,
            "name": "Dinning Table",
            "available": true,
            "stock": 1,
            "contain_articles": [
                {
                    "art_id": 1,
                    "amount_of": 4
                },
                {
                    "art_id": 2,
                    "amount_of": 8
                },
                {
                    "art_id": 4,
                    "amount_of": 1
                }
            ]
        }
    ]
}
```
#### GET /products/{id}
Gets the products by id
- Returns 200
- Returns 404 if the product is not present
```
curl -i -X 'GET' 'http://localhost:8080/products/1'
```

```
{
    "id": 1,
    "name": "Dining Chair",
    "available": true,
    "stock": 2,
    "contain_articles": [
        {
            "art_id": 1,
            "amount_of": 4
        },
        {
            "art_id": 2,
            "amount_of": 8
        },
        {
            "art_id": 3,
            "amount_of": 1
        }
    ]
}
```
#### POST /products
Creates a product
- Returns 201 Created
- Returns 400 Bad Request
```
curl -i -X 'POST' 'http://localhost:8080/products' -H "Content-Type: application/json"  -d '{"name":"extraRack","contain_articles":[{"art_id":4,"amount_of":1}]}'
```
#### PUT /products/{id}
Updates a product
- Returns 204 No Content
- Returns 400 Bad Request
```
curl -i -X 'POST' 'http://localhost:8080/products/1' -H "Content-Type: application/json"  -d '{"name":"extraRack","contain_articles":[{"art_id":4,"amount_of":1}]}'
```
#### DELETE /products/{id}
Removes the product from the system
- Returns 204 No Content
- Returns 404 Not found
```
curl -i -X 'DELETE' 'http://localhost:8080/products/1'
```
#### POST /products/{id}
Updates the inventory to reflect the stock
- Returns 204 No Content
```
curl -i -X 'POST' 'http://localhost:8080/products/1'
```



