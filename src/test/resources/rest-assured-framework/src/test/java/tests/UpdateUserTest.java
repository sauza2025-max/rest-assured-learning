package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import utils.Payloads;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UpdateUserTest extends BaseTest {

    @Test
    public void updateUser() {
        given()
            .header("Content-Type", "application/json")
            .body(Payloads.updateUser())
        .when()
            .put("/users/2")
        .then()
            .statusCode(200)
            .body("name", equalTo("John Updated"));
    }
}
