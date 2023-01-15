import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)

public class TestInvalidLoginParametrized {

    String token;
    String email;
    String password;
    String name = "Monkey D. Luffy";

    User user = new User("mugiwara@op.com", "kingofpirates", name);

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
    public void checkIfInvalidPasswordOrEmailBlockAuth() {
        User user = new User(email, password, name);
        given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void deleteUserAfterCreation() {
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
