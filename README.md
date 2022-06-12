### Run the application using below command:

``mvn spring-boot:run``

### Access the API documentation here:
[Swagger Docs](http://localhost:8080/swagger-ui/index.html#)

### Considerations for future increments 

- Strict Validations & Authorizations on Request Payloads
- Concurrency Tests and further improvements
- Though the file processing is done asynchronously, have to improve if the file sizes are huge
- Metrics implementation for Monitoring
- Proper functional responses for different error situations and also success cases
- Pagination for getProducts and getArticles

The focus was mainly getting the core functionality ready given the timelines and it is a simple application that can be deployed to Prod. But If we had more time the above improvements will make the app more adaptable for Prodcution.


