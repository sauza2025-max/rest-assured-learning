package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import models.Post;
import models.User;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * 📖 LESSON 1: GET Requests
 *
 * Covers:
 * ✅ Basic GET request
 * ✅ Path parameters
 * ✅ Query parameters
 * ✅ Response extraction (as object, as list, as string)
 * ✅ Chaining assertions with .body()
 * ✅ Hamcrest matchers
 */
@Feature("GET Requests")
public class GetRequestsTest extends BaseTest {

    // ─────────────────────────────────────────────────────────
    // 1️⃣ Basic GET — verify status code and content type
    // ─────────────────────────────────────────────────────────
    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify fetching all posts returns 200 and a non-empty list")
    public void getAllPosts_shouldReturn200AndNonEmptyList() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            // Hamcrest: hasSize checks list size
            .body("$", hasSize(100))
            // Hamcrest: everyItem checks all items
            .body("id", everyItem(notNullValue()))
            .body("title", everyItem(not(emptyString())));
    }

    // ─────────────────────────────────────────────────────────
    // 2️⃣ Path Parameters — GET /posts/{id}
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Fetch a single post by ID using path parameter")
    public void getPostById_shouldReturnCorrectPost() {
        int postId = 1;

        given()
            .spec(requestSpec)
            .pathParam("id", postId)   // 👈 Define path param
        .when()
            .get("/posts/{id}")         // 👈 Use it in the URL
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(postId))
            .body("userId", equalTo(1))
            .body("title", not(emptyString()));
    }

    // ─────────────────────────────────────────────────────────
    // 3️⃣ Query Parameters — GET /posts?userId=1
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Filter posts by userId using query parameter")
    public void getPostsByUserId_shouldReturnFilteredPosts() {
        int userId = 1;

        given()
            .spec(requestSpec)
            .queryParam("userId", userId)   // 👈 Adds ?userId=1 to the URL
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            // Every item in the list should have userId == 1
            .body("userId", everyItem(equalTo(userId)))
            .body("$", hasSize(greaterThan(0)));
    }

    // ─────────────────────────────────────────────────────────
    // 4️⃣ Extracting Response — as a POJO (Plain Old Java Object)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Extract response body and deserialize into a Post object")
    public void getPost_shouldDeserializeIntoPojoCorrectly() {
        // Extract the response as a Post object
        Post post = given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .extract()
            .as(Post.class);    // 👈 Jackson deserializes JSON → Post

        // Now use TestNG assertions on the POJO
        assertNotNull(post);
        assertEquals(post.getId(), Integer.valueOf(1));
        assertNotNull(post.getTitle());
        assertFalse(post.getBody().isEmpty());
    }

    // ─────────────────────────────────────────────────────────
    // 5️⃣ Extracting a LIST of objects
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Extract response as a list of Post objects")
    public void getAllPosts_shouldExtractAsList() {
        List<Post> posts = given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList(".", Post.class);  // 👈 Deserialize array → List<Post>

        assertFalse(posts.isEmpty());
        assertEquals(posts.size(), 100);
        posts.forEach(p -> assertNotNull(p.getId()));
    }

    // ─────────────────────────────────────────────────────────
    // 6️⃣ Extracting specific fields with JsonPath
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Use JsonPath to extract specific fields from response")
    public void getPost_shouldExtractSpecificFieldWithJsonPath() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .extract()
            .response();

        // JsonPath: navigate the JSON tree
        String title = response.jsonPath().getString("title");
        int userId   = response.jsonPath().getInt("userId");

        assertNotNull(title);
        assertEquals(userId, 1);
        System.out.println("✅ Extracted title: " + title);
    }

    // ─────────────────────────────────────────────────────────
    // 7️⃣ 404 — Handling not found
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Verify that a non-existent resource returns 404")
    public void getNonExistentPost_shouldReturn404() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/9999")
        .then()
            .statusCode(404);
    }

    // ─────────────────────────────────────────────────────────
    // 8️⃣ Nested JSON — accessing nested fields
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Fetch users and validate nested JSON fields")
    public void getUser_shouldAccessNestedJsonFields() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users/1")
        .then()
            .statusCode(200)
            // Dot notation navigates nested JSON: address.city
            .body("address.city", not(emptyString()))
            .body("address.geo.lat", not(emptyString()))
            .body("company.name", not(emptyString()));
    }
}
