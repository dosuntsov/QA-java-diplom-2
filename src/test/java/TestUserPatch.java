import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestUserPatch {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    String email = (RandomStringUtils.randomAlphabetic(10) + "@mail.ru").toLowerCase();
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);

    UserClient user = new UserClient(email, password, name);
    UserClient userSameEmail;

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
    public void verifyChangingPassword() {
        Response response = user.getPatchResponse(email, "newPassword", name);
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(name))
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    public void verifyChangingName() {
        Response response = user.getPatchResponse(email, password, "newName");
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo("newName"))
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    public void verifyChangingNewEmail() {
        Response response = user.getPatchResponse("newemail", password, name);
        response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(name))
                .and()
                .body("user.email", equalTo("newemail"));
    }

    @Test
    public void verifyChangingToExistingEmailFails() {
        String email = "test2@mail.com";
        userSameEmail = new UserClient(email, "newPassword", "newName");
        Response response = userSameEmail.getRegisterResponse();
        String token2 = response
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
        userSameEmail.setToken(token2);

        Response response2 = user.getPatchResponse(email, password, name);
        response2
                .then()
                .statusCode(403)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("User with such email already exists"));

    }

    @Test
    public void verifyUnauthorizedUserCannotPatch() {
        String token = user.getToken();
        user.setToken(null);
        Response response = user.getPatchResponse(email, password, name);
        response
                .then()
                .statusCode(401)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
        user.setToken(token);
    }

    @After
    public void deleteUserAfterTest() {
        user.deleteUser();
        if (userSameEmail != null) {
            userSameEmail.deleteUser();
        }
    }
}
