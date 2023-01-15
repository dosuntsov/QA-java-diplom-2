import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestClassGetOrders {
    String token;
    User user = new User("mugiwara@op.com", "kingofpirates", "Monkey D. Luffy");
    List<String> list = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa71", "61c0c5a71d1f82001bdaaa6d");
    Ingredients ingredients = new Ingredients(list);

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        createAUser();
        createAnOrder();
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

    public void createAnOrder() {
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
    public void verifyUnauthorisedUserDoesntGetOrderList() {
        given()
                .header("Content-type", "application/json")
                .get("/api/orders")
                .then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    public void verifyThatAuthorisedUserGetsOrderList() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .get("/api/orders")
                .then()
                .statusCode(200)
                .and().body("success", equalTo(true))
                .and().body("orders.id", notNullValue())
                .and().body("orders.ingredients", notNullValue())
                .and().body("orders.status", notNullValue())
                .and().body("orders.name", notNullValue())
                .and().body("orders.createdAt", notNullValue())
                .and().body("orders.updatedAt", notNullValue())
                .and().body("orders.number", notNullValue())
                .and().body("total", notNullValue())
                .and().body("totalToday", notNullValue());
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
