package es.upsa.dasi.tfg.aggregator;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class HealthResourceTest
{
    @Test
    void healthEsPublico()
    {
        given()
                .when().get("/api/health")
                .then()
                .statusCode(200)
                .body("status", notNullValue());
    }
}
