import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserClient;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)

public class TestInvalidLoginParametrized {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    String email = (RandomStringUtils.randomAlphabetic(10) + "@mail.ru").toLowerCase();
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);

    UserClient user = new UserClient(email, password, name);

    public TestInvalidLoginParametrized(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] variableNullsInQuery() {
        String email = "mugiwara@op.com";
        String password = "kingofpirates";
        return new Object[][]{
                {email, "blabla"},
                {"mugiwara@op.com1", password},
                {"email", "password"},
        };
    }

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
    public void checkIfInvalidPasswordOrEmailBlockAuth() {
        UserClient userWithInvalidParameters = new UserClient(email, password, name);
        Response response = userWithInvalidParameters.getLoginResponse();
        response
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void deleteUserAfterCreation() {
        user.deleteUser();
    }

}
