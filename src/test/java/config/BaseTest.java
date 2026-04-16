package config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

/**
 * Base configuration for all REST Assured tests.
 *
 * 📚 LEARNING NOTES:
 * - RequestSpecification: Pre-configure common request settings (base URI, headers, etc.)
 * - ResponseSpecification: Pre-configure common response assertions
 * - Using specs avoids repeating boilerplate in every test
 */
public class BaseTest {

    // The public API we'll use for learning — no auth required!
    protected static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    protected static final String REQRES_URL = "https://reqres.in/api";

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeClass
    public void setUp() {
        // ✅ RequestSpecBuilder: Define common request settings
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)  // Log all request details for learning
                .build();

        // ✅ ResponseSpecBuilder: Define common response expectations
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)  // Log all response details for learning
                .build();

        // Global config — sets default base URI for RestAssured.given()
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
