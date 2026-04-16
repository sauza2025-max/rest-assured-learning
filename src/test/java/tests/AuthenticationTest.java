package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * 📖 LESSON 3: Authentication & Headers
 *
 * Covers:
 * ✅ Bearer token (extracted from login response)
 * ✅ Basic Auth
 * ✅ Custom headers
 * ✅ Cookie authentication
 * ✅ Reusing auth in RequestSpec
 *
 * Uses: https://reqres.in — a free, hosted REST API designed for testing.
 */
@Feature("Authentication")
public class AuthenticationTest {

    private RequestSpecification reqresSpec;

    @BeforeClass
    public void setUp() {
        reqresSpec = new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api")
                .setContentType(ContentType.JSON)
                .build();
    }

    // ─────────────────────────────────────────────────────────
    // 1️⃣ Login & Extract Bearer Token
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Login and extract the bearer token from response")
    public void login_shouldReturnToken() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "eve.holt@reqres.in");
        credentials.put("password", "cityslicka");

        // Extract the token from the login response
        String token = given()
            .spec(reqresSpec)
            .body(credentials)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .extract()
            .jsonPath()
            .getString("token");

        System.out.println("✅ Extracted token: " + token);

        // Now use the token in a subsequent request
        given()
            .spec(reqresSpec)
            .header("Authorization", "Bearer " + token)  // 👈 Bearer token
        .when()
            .get("/users/2")
        .then()
            .statusCode(200)
            .body("data.id", equalTo(2));
    }

    // ─────────────────────────────────────────────────────────
    // 2️⃣ Failed Login — Invalid credentials
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Verify 400 is returned for missing password")
    public void login_withMissingPassword_shouldReturn400() {
        Map<String, String> badCredentials = new HashMap<>();
        badCredentials.put("email", "peter@klaven");
        // No password!

        given()
            .spec(reqresSpec)
            .body(badCredentials)
        .when()
            .post("/login")
        .then()
            .statusCode(400)
            .body("error", equalTo("Missing password"));
    }

    // ─────────────────────────────────────────────────────────
    // 3️⃣ Custom Headers
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Send request with custom headers")
    public void request_withCustomHeaders_shouldSucceed() {
        given()
            .spec(reqresSpec)
            .header("X-Custom-Header", "learning-rest-assured")
            .header("X-Request-ID", "test-12345")
            .header("Accept-Language", "en-US")
        .when()
            .get("/users?page=1")
        .then()
            .statusCode(200)
            .body("page", equalTo(1));
    }

    // ─────────────────────────────────────────────────────────
    // 4️⃣ Basic Auth (using REST Assured built-in)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Demonstrate Basic Auth configuration")
    public void request_withBasicAuth() {
        // Basic Auth is built into REST Assured
        // This hits JSONPlaceholder which doesn't enforce auth,
        // but demonstrates the syntax
        given()
            .auth()
            .basic("username", "password")   // 👈 Sets Authorization: Basic <encoded>
            .baseUri("https://jsonplaceholder.typicode.com")
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200);  // JSONPlaceholder ignores the auth, but syntax is correct

        System.out.println("✅ Basic Auth syntax: .auth().basic(user, pass)");
    }

    // ─────────────────────────────────────────────────────────
    // 5️⃣ Preemptive Auth (send without waiting for 401 challenge)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Use preemptive auth to send credentials without waiting for challenge")
    public void request_withPreemptiveBasicAuth() {
        given()
            .auth()
            .preemptive()                    // 👈 Don't wait for 401 challenge
            .basic("username", "password")
            .baseUri("https://jsonplaceholder.typicode.com")
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200);

        System.out.println("✅ Preemptive auth sends credentials on first request");
    }
}
