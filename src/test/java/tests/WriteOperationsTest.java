package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.Post;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * 📖 LESSON 2: POST, PUT, PATCH, DELETE
 *
 * Covers:
 * ✅ POST with JSON body (as POJO)
 * ✅ POST with JSON body (as Map)
 * ✅ PUT — full update
 * ✅ PATCH — partial update
 * ✅ DELETE
 * ✅ Extracting created resource ID
 *
 * Note: JSONPlaceholder simulates these operations but doesn't persist data.
 * It returns realistic responses for learning purposes.
 */
@Feature("Write Operations (POST, PUT, PATCH, DELETE)")
public class WriteOperationsTest extends BaseTest {

    // ─────────────────────────────────────────────────────────
    // 1️⃣ POST — Send a POJO as request body
    // ─────────────────────────────────────────────────────────
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new post using a POJO as request body")
    public void createPost_withPojo_shouldReturn201() {
        // Build the request body using Lombok's @Builder
        Post newPost = Post.builder()
                .userId(1)
                .title("Learning REST Assured")
                .body("REST Assured makes API testing simple and readable!")
                .build();

        // Send POST and extract the created post
        Post createdPost = given()
            .spec(requestSpec)
            .body(newPost)              // 👈 Jackson serializes Post → JSON
        .when()
            .post("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(201)            // 201 Created
            .body("title", equalTo(newPost.getTitle()))
            .body("userId", equalTo(newPost.getUserId()))
            .extract()
            .as(Post.class);

        // The API assigns an ID to the new resource
        assertNotNull(createdPost.getId());
        assertEquals(createdPost.getTitle(), newPost.getTitle());
        System.out.println("✅ Created Post ID: " + createdPost.getId());
    }

    // ─────────────────────────────────────────────────────────
    // 2️⃣ POST — Send a Map as request body (useful for dynamic data)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Create a post using a Map as request body")
    public void createPost_withMap_shouldReturn201() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", 2);
        requestBody.put("title", "Map-based request body");
        requestBody.put("body", "Using a Map gives flexibility for dynamic payloads");

        given()
            .spec(requestSpec)
            .body(requestBody)          // 👈 Jackson serializes Map → JSON
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("Map-based request body"));
    }

    // ─────────────────────────────────────────────────────────
    // 3️⃣ PUT — Full update (replaces the entire resource)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Fully update a post using PUT")
    public void updatePost_withPut_shouldReturn200() {
        Post updatedPost = Post.builder()
                .id(1)
                .userId(1)
                .title("Updated Title via PUT")
                .body("The entire resource is replaced with PUT")
                .build();

        given()
            .spec(requestSpec)
            .body(updatedPost)
        .when()
            .put("/posts/1")            // 👈 PUT to specific resource
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("Updated Title via PUT"));
    }

    // ─────────────────────────────────────────────────────────
    // 4️⃣ PATCH — Partial update (only sends changed fields)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Partially update a post title using PATCH")
    public void updatePostTitle_withPatch_shouldReturn200() {
        // Only send the field we want to update
        Map<String, String> partialUpdate = new HashMap<>();
        partialUpdate.put("title", "Just the title changed via PATCH");

        given()
            .spec(requestSpec)
            .body(partialUpdate)
        .when()
            .patch("/posts/1")          // 👈 PATCH for partial updates
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("Just the title changed via PATCH"));
    }

    // ─────────────────────────────────────────────────────────
    // 5️⃣ DELETE — Remove a resource
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Delete a post and verify 200 response")
    public void deletePost_shouldReturn200() {
        given()
            .spec(requestSpec)
        .when()
            .delete("/posts/1")         // 👈 DELETE request
        .then()
            .statusCode(200);           // JSONPlaceholder returns 200 for DELETE

        System.out.println("✅ Post deleted successfully");
    }

    // ─────────────────────────────────────────────────────────
    // 6️⃣ Chaining — Create then verify (simulated flow)
    // ─────────────────────────────────────────────────────────
    @Test
    @Description("Demonstrate a create-and-verify workflow")
    public void createAndVerifyPost_workflow() {
        // Step 1: Create the post
        Post newPost = Post.builder()
                .userId(5)
                .title("Workflow Test Post")
                .body("Testing a complete workflow")
                .build();

        int createdId = given()
            .spec(requestSpec)
            .body(newPost)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getInt("id");              // 👈 Extract just the ID

        assertTrue(createdId > 0, "Expected a positive ID to be assigned");
        System.out.println("✅ Created resource with ID: " + createdId);

        // Step 2: In a real API, you'd then GET the created resource to verify
        // (JSONPlaceholder doesn't persist, but this pattern is the key concept)
        System.out.println("ℹ️ In a real API: GET /posts/" + createdId + " to verify");
    }
}
