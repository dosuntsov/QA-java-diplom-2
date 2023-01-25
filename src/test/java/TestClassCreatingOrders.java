import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.OrderClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestClassCreatingOrders {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    String email = (RandomStringUtils.randomAlphabetic(10) + "@mail.ru").toLowerCase();
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);
    UserClient user = new UserClient(email, password, name);
    List<String> list = Arrays.asList("61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa71", "61c0c5a71d1f82001bdaaa6d");
    List<String> invalidList = Arrays.asList("1337c5a71d1f82001bdaaa75", "1337c5a71d1f82001bdaaa71", "1337c5a71d1f82001bdaaa6d");
    OrderClient order = new OrderClient(list);

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        createAUser();
    }

    public void createAUser() {
        Response response = user.getRegisterResponse();
        String token = response
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
        user.setToken(token);
        order.setToken(token);
    }

    @Test
    public void checkIfCreatingAnOrderAuthorizedIsAvaliable() {
        Response response = order.getResponseCreateAnOrder();
        response
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    public void checkIfCreatingAnOrderUnauthorized() {
        order.setToken(null);
        Response response = order.getResponseCreateAnOrder();
        response
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    public void checkIfCreatingANullOrderIsImpossible() {
        order.setNewList();
        Response response = order.getResponseCreateAnOrder();
        response
                .then()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void checkIfInvalidHashBlocksOrderCreation() {
        order.setNewList(invalidList);
        Response response = order.getResponseCreateAnOrder();
        response
                .then()
                .statusCode(500);
    }

    @After
    public void deleteUserAfterTest() {
        user.deleteUser();
    }

}
