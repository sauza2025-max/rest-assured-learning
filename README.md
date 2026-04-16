# 🧪 REST Assured Learning Project

A complete, hands-on Java project for learning API testing with **REST Assured**.  
Uses free public APIs — no setup, no auth keys needed to get started.

---

## 📁 Project Structure

```
rest-assured-learning/
├── pom.xml                          # Maven dependencies
└── src/test/
    ├── java/
    │   ├── config/
    │   │   └── BaseTest.java        # Shared setup (RequestSpec, ResponseSpec)
    │   ├── models/
    │   │   ├── Post.java            # Lombok POJO for Posts
    │   │   └── User.java            # Lombok POJO for Users
    │   └── tests/
    │       ├── GetRequestsTest.java         # LESSON 1: GET requests
    │       ├── WriteOperationsTest.java     # LESSON 2: POST / PUT / PATCH / DELETE
    │       ├── AuthenticationTest.java      # LESSON 3: Auth & Headers
    │       └── AdvancedAssertionsTest.java  # LESSON 4: Advanced validation
    └── resources/
        ├── testng.xml               # TestNG suite config
        └── schemas/
            └── post-schema.json     # JSON Schema for validation
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Run All Tests
```bash
mvn test
```

### Run a Single Test Class
```bash
mvn test -Dtest=GetRequestsTest
```

### Run a Single Test Method
```bash
mvn test -Dtest=GetRequestsTest#getPostById_shouldReturnCorrectPost
```

### Generate Allure Report
```bash
mvn allure:serve
```

---

## 📚 Learning Path

### Lesson 1 — GET Requests (`GetRequestsTest.java`)
| Concept | Method |
|---|---|
| Basic GET + status code | `getAllPosts_shouldReturn200AndNonEmptyList` |
| Path parameters | `getPostById_shouldReturnCorrectPost` |
| Query parameters | `getPostsByUserId_shouldReturnFilteredPosts` |
| Deserialize to POJO | `getPost_shouldDeserializeIntoPojoCorrectly` |
| Deserialize to List | `getAllPosts_shouldExtractAsList` |
| JsonPath extraction | `getPost_shouldExtractSpecificFieldWithJsonPath` |
| 404 handling | `getNonExistentPost_shouldReturn404` |
| Nested JSON | `getUser_shouldAccessNestedJsonFields` |

### Lesson 2 — Write Operations (`WriteOperationsTest.java`)
| Concept | Method |
|---|---|
| POST with POJO body | `createPost_withPojo_shouldReturn201` |
| POST with Map body | `createPost_withMap_shouldReturn201` |
| Full update with PUT | `updatePost_withPut_shouldReturn200` |
| Partial update with PATCH | `updatePostTitle_withPatch_shouldReturn200` |
| DELETE request | `deletePost_shouldReturn200` |
| Create → verify flow | `createAndVerifyPost_workflow` |

### Lesson 3 — Authentication (`AuthenticationTest.java`)
| Concept | Method |
|---|---|
| Login + extract token | `login_shouldReturnToken` |
| Failed login | `login_withMissingPassword_shouldReturn400` |
| Custom headers | `request_withCustomHeaders_shouldSucceed` |
| Basic Auth | `request_withBasicAuth` |
| Preemptive Auth | `request_withPreemptiveBasicAuth` |

### Lesson 4 — Advanced Assertions (`AdvancedAssertionsTest.java`)
| Concept | Method |
|---|---|
| Hamcrest matchers | `hamcrestMatchers_deepDive` |
| Array/collection assertions | `collectionAssertions_onArrayResponse` |
| Response time validation | `responseTime_shouldBeWithinLimit` |
| Header assertions | `responseHeaders_shouldContainExpectedValues` |
| JSON Schema validation | `getPost_shouldMatchJsonSchema` |
| Nested JSON assertions | `nestedJson_assertOnDeepFields` |
| Soft assertions | `softAssertions_collectMultipleFailures` |

---

## 🔑 Key Concepts

### RequestSpecification
Pre-configure common settings to avoid repetition:
```java
requestSpec = new RequestSpecBuilder()
    .setBaseUri("https://jsonplaceholder.typicode.com")
    .setContentType(ContentType.JSON)
    .log(LogDetail.ALL)
    .build();
```

### REST Assured Syntax
```java
given()          // Set up: headers, body, auth, params
    .spec(requestSpec)
    .pathParam("id", 1)
.when()          // Action: GET, POST, PUT, PATCH, DELETE
    .get("/posts/{id}")
.then()          // Assert: status, body, headers
    .statusCode(200)
    .body("title", notNullValue());
```

### Common Hamcrest Matchers
```java
equalTo(value)          // Exact match
notNullValue()          // Not null
not(emptyString())      // Not empty string
greaterThan(0)          // Numeric comparison
hasSize(100)            // Collection size
everyItem(notNullValue()) // All items match
hasItem(1)              // Contains item
containsString("text")  // String contains
```

### Extracting Values
```java
// Extract as POJO
Post post = response.extract().as(Post.class);

// Extract specific field
String title = response.extract().jsonPath().getString("title");

// Extract list
List<Post> posts = response.extract().jsonPath().getList(".", Post.class);
```

---

## 🌐 APIs Used

| API | URL | Purpose |
|---|---|---|
| JSONPlaceholder | https://jsonplaceholder.typicode.com | GET / POST / PUT / PATCH / DELETE (fake data) |
| ReqRes | https://reqres.in | Auth flows (login, registration) |

---

## 📦 Dependencies

| Library | Purpose |
|---|---|
| `rest-assured` | HTTP client + fluent assertions |
| `json-schema-validator` | JSON Schema validation |
| `testng` | Test framework |
| `jackson-databind` | JSON serialization/deserialization |
| `lombok` | Reduces boilerplate (@Data, @Builder) |
| `allure-testng` | Beautiful HTML test reports |

---

## 💡 Next Steps

After mastering the basics, explore:
- **Request filters** — intercept/modify all requests globally
- **Multipart uploads** — file upload testing
- **OAuth2** — token refresh flows
- **GraphQL** — testing GraphQL APIs with REST Assured
- **Contract testing** with Pact
- **Data-driven tests** with TestNG `@DataProvider`
