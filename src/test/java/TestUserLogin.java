import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestUserLogin {
    String token;
    User user = new User("mugiwara@op.com", "kingofpirates", "Monkey D. Luffy");

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
    public void loginValidUserIsSuccessful() {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/login")
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
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name));
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
