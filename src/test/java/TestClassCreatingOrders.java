import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestClassCreatingOrders {
    String token;
    User user = new User("mugiwara@op.com", "kingofpirates", "Monkey D. Luffy");
    List<String> list = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa71", "61c0c5a71d1f82001bdaaa6d");
    List<String> invalidList = Arrays.asList("1337c5a71d1f82001bdaaa75", "1337c5a71d1f82001bdaaa71", "1337c5a71d1f82001bdaaa6d");
    Ingredients ingredients = new Ingredients(list);
    Ingredients ingredients2 = new Ingredients();


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        createAUser();
    }

    public void createAUser() {
        token = given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    public void checkIfCreatingAnOrderAuthorizedIsAvaliable() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(ingredients)
                .post("/api/orders")
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    public void checkIfCreatingAnOrderUnauthorized() {
        given()
                .header("Content-type", "application/json")
                .body(ingredients)
                .post("/api/orders")
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    public void checkIfCreatingANullOrderIsImpossible() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(ingredients2)
                .post("/api/orders")
                .then()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void checkIfInvalidHashBlocksOrderCreation() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(invalidList)
                .post("/api/orders")
                .then()
                .statusCode(500);
    }

    @After
    public void deleteUserAfterTest() {
        given()
                .header("Authorization", token)
                .delete("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(202)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("message", equalTo("User successfully removed"));
    }

}
