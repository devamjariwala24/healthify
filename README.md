# Healthify Project Development Notes

## Project Overview
We are building a healthcare cost calculator and chatbot system for Premera. The main idea is to help users understand their insurance costs by calculating what they would pay for different medical services based on their insurance plan.

## Major Decision: Spring Boot Architecture

We chose Spring Boot because it is the industry standard for building REST APIs in Java. Almost every company uses it, including Premera. The architecture we used is called layered architecture which separates different concerns into different folders.

## Why Layered Architecture?

We organized code into 6 layers. Each layer has one specific job. This makes the code easy to understand, test, and modify. If you need to change how data is stored, you only touch the repository layer. If you need to change business rules, you only touch the service layer.

The layers are:
1. Entity - Defines what the database tables look like
2. Repository - Talks to the database
3. DTO - Defines what data the API sends and receives
4. Service - Contains all business logic
5. Controller - Handles HTTP requests
6. Exception - Handles errors properly

## Entity Layer Decisions

### Why use JPA annotations like @Entity and @Table?
Spring needs to know which Java classes represent database tables. These annotations tell Spring to automatically create tables from your Java classes. Without them, you would have to write SQL CREATE TABLE statements manually every time.

### Why Long for ID instead of int?
Long can be null before saving to database. int cannot be null and defaults to 0 which causes confusion. Also Long can store much bigger numbers. IDs in real applications can get very large. Long is the standard practice for database IDs in Spring Boot.

### Why BigDecimal for money instead of double?
This is critical. Never use double or float for money. They have precision errors. For example, 0.1 plus 0.2 equals 0.30000000000004 with double. With BigDecimal you get exactly 0.3. Financial applications must be precise to the cent. Using double for money can cause legal issues and accounting nightmares.

### Why nullable equals false on some columns?
This makes certain fields required. The database will reject any insert or update that does not include these fields. This prevents bad data from getting into your database. For example, every insurance plan must have a name and premium. Making these not nullable enforces this rule at the database level.

### Why did we add @Data, @NoArgsConstructor, @AllArgsConstructor?
Lombok generates code automatically. @Data creates getters, setters, toString, equals, and hashCode. Without Lombok you would write 50 plus lines of boilerplate code for each entity. @NoArgsConstructor creates an empty constructor which JPA needs. @AllArgsConstructor creates a constructor with all fields which is convenient for testing.

## Repository Layer Decisions

### Why use interface instead of class?
Spring Data JPA is magic. You write an interface that extends JpaRepository and Spring automatically creates the implementation at runtime. You get 20 plus methods for free like save, findById, findAll, deleteById without writing any code. This saves hundreds of lines of code.

### Why does the interface extend JpaRepository<EntityType, IDType>?
The first parameter tells Spring which entity this repository works with. The second parameter tells Spring what type the ID field is. This allows Spring to generate the correct SQL queries automatically.

### Why use Optional<Entity> as return type?
When you search for something by ID it might not exist. In the old days we would return null and then get NullPointerException everywhere. Optional forces you to check if the value exists before using it. This prevents null pointer exceptions and makes code safer.

### Why use method names like findByPlanName?
Spring reads your method name and generates SQL automatically. findBy means SELECT query. PlanName means the planName field in your entity. Spring converts this to SELECT * FROM insurance_plans WHERE plan_name = ?. You write zero SQL but get working queries. This only works if you follow the naming convention exactly.

### Why use List<Entity> for methods that return multiple results?
When searching by criteria like price or category, multiple records can match. List holds multiple entities. If you use Optional or just Entity, Spring will fail if more than one result comes back.

## DTO Layer Decisions

### Why do we need DTOs when we have entities?
Entities are internal. They have database annotations and sometimes relationships to other entities. If you send entities directly to the frontend you expose your internal database structure. DTOs are external. They define exactly what data the API sends and receives. This separates your API contract from your database schema. You can change the database without breaking the API.

### Why no JPA annotations in DTOs?
DTOs are not database tables. They are just containers for data that travels between frontend and backend. JPA annotations like @Entity and @Column would confuse Spring and cause errors.

### Why use the same field names in DTO and Entity?
This makes conversion between DTO and Entity easier. In the service layer you copy fields from Entity to DTO and vice versa. If names match you can even use libraries like MapStruct to automate this. Keeping names consistent reduces bugs.

## Exception Layer Decisions

### Why create custom exceptions like ResourceNotFoundException?
Spring has generic exceptions but they return ugly error messages and wrong status codes. When a record is not found we want to return HTTP 404 with a nice message. Custom exceptions let us control exactly what error response the user sees.

### Why use @RestControllerAdvice for GlobalExceptionHandler?
This annotation makes the exception handler apply to all controllers in the application. Without it you would have to handle exceptions in every single controller method. This is the DRY principle. Write the error handling logic once and it works everywhere.

### Why return different HTTP status codes?
HTTP status codes have meaning. 404 means not found. 500 means server error. 400 means bad request. Clients like mobile apps and frontend code check these status codes to decide what to do. Returning the correct status code is part of good API design.

## Service Layer Decisions

### Why use @Transactional?
Transactions ensure data consistency. If you update multiple database tables and one fails, the transaction rolls back all changes. Without transactions you can end up with partial data in the database which causes data corruption. For example, if you deduct money from one account but fail to add it to another account, you lose money. Transactions prevent this.

### Why use constructor injection with @RequiredArgsConstructor instead of @Autowired?
Constructor injection is modern best practice. It makes dependencies explicit. You can see what the class needs just by looking at the constructor. It also makes testing easier because you can create the class by just calling the constructor with mock objects. Field injection with @Autowired hides dependencies and makes testing harder. It also allows the field to be null which can cause NullPointerException. Constructor injection with final fields guarantees the dependency is never null.

### Why create separate methods for convertToDTO and convertToEntity?
The repository only works with entities. The controller only works with DTOs. The service layer is in the middle so it has to convert between them. Putting conversion logic in separate methods keeps the CRUD methods clean and easy to read. It also means if you change the DTO structure you only update the conversion methods.

### Why throw exceptions in service layer instead of returning null?
Returning null forces every caller to check for null. If they forget you get NullPointerException at runtime. Throwing exceptions forces the caller to handle the error. Our GlobalExceptionHandler catches these exceptions and returns proper HTTP responses automatically. This makes error handling consistent across the whole application.

## Controller Layer Decisions

### Why use @RestController instead of @Controller?
@RestController combines @Controller and @ResponseBody. It tells Spring to convert return values to JSON automatically. With @Controller you would have to add @ResponseBody to every method. @RestController saves repetition and is the standard for REST APIs.

### Why use @RequestMapping at class level?
All endpoints in a controller usually start with the same base path. Putting @RequestMapping("/api/insurance-plans") at class level means all methods inherit this path. This keeps code DRY and makes URLs consistent.

### Why use @PostMapping, @GetMapping, @PutMapping, @DeleteMapping?
These are shortcuts for @RequestMapping with method specified. @GetMapping is cleaner than @RequestMapping(method = RequestMethod.GET). It also makes the HTTP method obvious at a glance when reading code.

### Why use @PathVariable for IDs in URL?
REST best practice is to put IDs in the URL path not in query parameters. /api/insurance-plans/5 is better than /api/insurance-plans?id=5. PathVariable extracts the ID from the URL path and gives it to your method as a parameter.

### Why use @RequestBody for POST and PUT?
POST and PUT requests send data in the request body as JSON. @RequestBody tells Spring to parse the JSON and convert it to your DTO object automatically. Without this annotation you would have to manually parse JSON which is tedious and error prone.

### Why use ResponseEntity instead of returning DTO directly?
ResponseEntity gives you control over the HTTP response. You can set the status code, add headers, and include the body. Returning just the DTO gives you 200 status by default which is wrong for POST which should return 201 Created. ResponseEntity lets you follow HTTP standards properly.

### Why return HttpStatus.CREATED for POST?
HTTP standards say successful resource creation should return 201 Created not 200 OK. Following standards makes your API predictable and easier for clients to use. Many frontend frameworks expect 201 for POST.

### Why return HttpStatus.NO_CONTENT for DELETE?
When you delete something there is nothing to return. 204 No Content is the standard way to say the delete succeeded but there is no response body. This is more semantically correct than 200 OK with an empty body.

## Database Configuration Decisions

### Why PostgreSQL instead of MySQL or H2?
PostgreSQL is what most modern companies use for production. It has better data integrity, supports JSON columns which we need for the chatbot, and handles concurrent users better than MySQL. H2 is only for testing. Learning PostgreSQL gives you skills that transfer directly to real jobs.

### Why create a separate database user instead of using the default postgres user?
Security best practice. The postgres superuser has all permissions. If your application gets hacked the attacker has full control of all databases. Creating a limited user with only permissions on one database follows the principle of least privilege. If the application is compromised the attacker can only access the healthify database.

### Why use spring.jpa.hibernate.ddl-auto equals update instead of create?
create drops and recreates tables every time you start the app. You lose all data. update only modifies the schema to match your entities. Existing data is preserved. In development update is convenient. In production you use validate which just checks the schema or none and handle schema changes with migration tools like Flyway.

### Why use spring.jpa.show-sql equals true?
During development you want to see what SQL Hibernate generates. This helps you understand what is happening and debug issues. In production you turn this off because logging every SQL query impacts performance and clutters logs.

## Enum Decisions for ServiceType

### Why use enum for category instead of String?
Enums are type safe. With String a user can enter anything like "medical", "Medical", "MEDICAL", or "medicul" with a typo. With enum only predefined values are allowed. The compiler checks this. You cannot even compile code that uses an invalid enum value. This prevents bad data and makes code safer.

### Why use @Enumerated(EnumType.STRING) instead of EnumType.ORDINAL?
ORDINAL stores the position like 0, 1, 2. If you add a new enum value in the middle or reorder them, all your database data becomes wrong. STRING stores the actual name like "MEDICAL". This is safe for refactoring. You can add new values or reorder without breaking existing data. STRING is always the right choice unless you have extreme storage constraints.

### How does enum validation work automatically?
When Spring receives JSON with an enum field it tries to convert the string to the enum type. If the value does not match any enum constant, Spring throws an exception. Our GlobalExceptionHandler catches this and returns an error. The user gets a clear message listing valid values. No extra validation code needed.

## Testing Strategy

### Why test with Postman instead of just using the browser?
Browsers can only easily do GET requests. For POST, PUT, DELETE you need to send JSON in the request body. Browsers make this awkward. Postman is built for testing APIs. It handles all HTTP methods, lets you easily send JSON, saves your requests for reuse, and shows responses clearly.

### Why test each CRUD operation separately?
You want to verify each operation works before moving to the next. If CREATE fails then READ will also fail because there is no data. Testing systematically helps you catch issues early and understand which part of your code has problems.

### Why verify data in DBeaver after API calls?
The API might return success but not actually save to the database. Checking DBeaver confirms the data really made it to the database. This catches bugs where transactions roll back or data is not committed properly.

## Development Process Lessons

### Why build one entity completely before starting the next?
Building InsurancePlan taught you the pattern. Building ServiceType let you practice applying the pattern yourself. This repetition with slight variations is how you learn. Trying to build everything at once is overwhelming and you cannot remember the details.

### Why I gave you hints instead of full solutions for ServiceType?
You learn by doing not by copying. Hints make you think about the problem. When you figure it out yourself the knowledge sticks in your brain much better than if I just gave you the answer. This is called active learning.

### Why we fixed small issues like method naming?
Consistency matters in real projects. When working with a team everyone needs to follow the same naming conventions. Fixing these details teaches you to write professional code from the start. Small sloppiness leads to big problems in large codebases.

## Key Takeaways for Future Reference

Never use double for money. Always use BigDecimal.

Always use Long for database IDs not int.

Constructor injection is better than field injection.

DTOs separate your API from your database.

Enums are better than Strings when you have a fixed set of values.

Repository interfaces give you free CRUD methods.

Service layer contains business logic, controller is thin.

Proper exception handling improves API quality significantly.

Test systematically, verify in the database, not just in the API.

Learning by doing with hints works better than copy paste.

## What We Built

Two complete REST APIs with full CRUD operations. InsurancePlan manages the six types of insurance plans users can choose from. ServiceType manages the medical services that insurance covers. Both APIs handle errors properly, validate data, and follow industry best practices. The code is production quality and follows patterns used at companies like Premera.

Next we are building PlanCoverage which is more complex because it has relationships to both InsurancePlan and ServiceType. This will teach you about foreign keys and joins.
