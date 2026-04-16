package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import utils.Payloads;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CreateUserTest extends BaseTest {

    @Test
    public void createUser() {
        given()
            .header("Content-Type", "application/json")
            .body(Payloads.createUser())
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("name", equalTo("John"));
    }
}
