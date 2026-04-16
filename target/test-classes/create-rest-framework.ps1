$base = "rest-assured-framework"

# Create folders
New-Item -ItemType Directory -Force -Path "$base\src\test\java\base" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\src\test\java\tests" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\src\test\java\utils" | Out-Null

# pom.xml
@"
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>rest-assured-framework</artifactId>
  <version>1.0-SNAPSHOT</version>

  <dependencies>
    <dependency>
      <groupId>io.rest-assured</groupId>
      <artifactId>rest-assured</artifactId>
      <version>5.5.0</version>
      <scope>test</scope>
    </dependency>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.17.0</version>
    </dependency>

    <dependency>
      <groupId>org.testng</groupId>
      <artifactId>testng</artifactId>
      <version>7.10.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
"@ | Set-Content "$base\pom.xml"

# BaseTest
@"
package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
    }
}
"@ | Set-Content "$base\src\test\java\base\BaseTest.java"

# Payloads
@"
package utils;

import java.util.HashMap;
import java.util.Map;

public class Payloads {

    public static Map<String, Object> createUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "John");
        body.put("job", "QA Engineer");
        return body;
    }

    public static Map<String, Object> updateUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "John Updated");
        body.put("job", "Senior QA");
        return body;
    }
}
"@ | Set-Content "$base\src\test\java\utils\Payloads.java"

# GET test
@"
package tests;

import base.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GetUserTest extends BaseTest {

    @Test
    public void getUser() {
        given()
        .when()
            .get("/users/2")
        .then()
            .statusCode(200)
            .body("data.id", equalTo(2));
    }
}
"@ | Set-Content "$base\src\test\java\tests\GetUserTest.java"

# POST test
@"
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
"@ | Set-Content "$base\src\test\java\tests\CreateUserTest.java"

# PUT test
@"
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
"@ | Set-Content "$base\src\test\java\tests\UpdateUserTest.java"

# ZIP file
Compress-Archive -Path $base -DestinationPath "rest-assured-framework.zip" -Force

Write-Host "DONE -> rest-assured-framework.zip created"