import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestUserLogin {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    String email = (RandomStringUtils.randomAlphabetic(10) + "@mail.ru").toLowerCase();
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);
    UserClient user = new UserClient(email, password, name);

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
    }


    @Test
    public void loginValidUserIsSuccessful() {
        Response response = user.getLoginResponse();
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user.email", equalTo(email))
                .and()
                .body("user.name", equalTo(name));
    }

    @After
    public void deleteUserAfterTest() {
       user.deleteUser();
    }
}
