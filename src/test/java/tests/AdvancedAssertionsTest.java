package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * 📖 LESSON 4: Advanced Assertions & Response Validation
 *
 * Covers:
 * ✅ Hamcrest matchers (deep dive)
 * ✅ Response time validation
 * ✅ JSON Schema validation
 * ✅ Header assertions
 * ✅ Array/collection assertions
 * ✅ Conditional / contains assertions
 */
@Feature("Advanced Assertions")
public class AdvancedAssertionsTest extends BaseTest {

    // ─────────────────────────────────────────────────────────
    // 1️⃣ Hamcrest Matchers — Deep Dive
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Demonstrate various Hamcrest matchers")
    public void hamcrestMatchers_deepDive() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)

            // Equality
            .body("id", equalTo(1))

            // Not null / not empty
            .body("title", notNullValue())
            .body("title", not(emptyString()))

            // Numeric comparisons
            .body("userId", greaterThan(0))
            .body("userId", lessThanOrEqualTo(10))
            .body("id", greaterThanOrEqualTo(1))

            // String matchers
            .body("title", containsString("sunt"))      // Contains substring
            // .body("title", startsWith("s"))          // Starts with
            // .body("title", endsWith("t"))            // Ends with
            .body("title", matchesPattern("[a-zA-Z ]+.*")); // Regex match
    }

    // ─────────────────────────────────────────────────────────
    // 2️⃣ Collection / Array Assertions
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Assert on arrays and collections in response")
    public void collectionAssertions_onArrayResponse() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)

            // Size assertions
            .body("$", hasSize(100))
            .body("$", hasSize(greaterThan(0)))

            // Every item in the array
            .body("id", everyItem(notNullValue()))
            .body("userId", everyItem(greaterThan(0)))

            // At least one item matches
            .body("userId", hasItem(1))              // Contains the value 1

            // Array contains items matching conditions
            .body("id", hasItems(1, 2, 3));          // Contains all these values
    }

    // ─────────────────────────────────────────────────────────
    // 3️⃣ Response Time Validation
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Verify the API responds within an acceptable time")
    public void responseTime_shouldBeWithinLimit() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            // Response must arrive within 5 seconds
            .time(lessThan(5000L));  // milliseconds
    }

    @Test
    @Description("Extract response time for manual assertions")
    public void responseTime_extractAndAssert() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .extract()
            .response();

        long responseTime = response.getTimeIn(TimeUnit.MILLISECONDS);
        System.out.println("⏱️ Response time: " + responseTime + "ms");
        assertTrue(responseTime < 5000, "Response too slow: " + responseTime + "ms");
    }

    // ─────────────────────────────────────────────────────────
    // 4️⃣ Header Assertions
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Validate response headers")
    public void responseHeaders_shouldContainExpectedValues() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"))
            .header("Content-Type", notNullValue());
    }

    @Test
    @Description("Extract and validate headers manually")
    public void responseHeaders_extractAndValidate() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .extract()
            .response();

        String contentType = response.getHeader("Content-Type");
        System.out.println("📋 Content-Type: " + contentType);
        assertTrue(contentType.contains("application/json"),
                "Expected JSON content type, got: " + contentType);
    }

    // ─────────────────────────────────────────────────────────
    // 5️⃣ JSON Schema Validation
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Validate response matches JSON Schema (requires schema file in resources)")
    public void getPost_shouldMatchJsonSchema() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            // Validates the response structure against a JSON Schema file
            .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    // ─────────────────────────────────────────────────────────
    // 6️⃣ Nested JSON Assertions
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Assert on deeply nested JSON values")
    public void nestedJson_assertOnDeepFields() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users/1")
        .then()
            .statusCode(200)
            // Dot notation for nested objects
            .body("address.city", not(emptyString()))
            .body("address.street", not(emptyString()))
            // Deeper nesting
            .body("address.geo.lat", not(emptyString()))
            .body("address.geo.lng", not(emptyString()))
            // Nested objects within arrays
            .body("company.name", not(emptyString()));
    }

    // ─────────────────────────────────────────────────────────
    // 7️⃣ Soft Assertions with TestNG (collect all failures)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Use soft assertions to collect multiple failures")
    public void softAssertions_collectMultipleFailures() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .extract()
            .response();

        // Soft asserts — all checks run even if one fails
        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        softAssert.assertEquals(response.jsonPath().getInt("id"), 1);
        softAssert.assertNotNull(response.jsonPath().getString("title"));
        softAssert.assertTrue(response.jsonPath().getString("body").length() > 0);
        softAssert.assertEquals(response.jsonPath().getInt("userId"), 1);

        softAssert.assertAll(); // 👈 Throws if any assertion failed
    }

    // ─────────────────────────────────────────────────────────
    // 8️⃣ Extracting values for use in assertions
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Extract list values and assert programmatically")
    public void extractList_andAssertProgrammatically() {
        List<Integer> userIds = given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("userId");

        // TestNG assertions on the extracted list
        assertFalse(userIds.isEmpty(), "userId list should not be empty");
        assertTrue(userIds.contains(1), "Should contain userId 1");
        assertEquals(userIds.size(), 100, "Should have 100 posts");

        long distinctUsers = userIds.stream().distinct().count();
        System.out.println("📊 Distinct users: " + distinctUsers);
    }
}
