import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestUserPatch {

    String token;
    String token2;
    String email = "mugiwara@op.com";
    String password = "kingofpirates";
    String name = "Monkey D. Luffy";

    User user = new User(email, password, name);

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        createAUser(user);
    }

    public Response getPatchResponse(User user) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(user)
                .patch("/api/auth/user");
    }

    public Response getRegisterResponse(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    public void createAUser(User user) {
        Response response = getRegisterResponse(user);
        token = response
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
    public void verifyChangingPassword() {
        User user1 = new User(email, "newPassword", name);
        Response response = getPatchResponse(user1);
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
        User user2 = new User(email, password, "newName");
        Response response = getPatchResponse(user2);
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
        User user1 = new User("newemail", password, name);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(user1)
                .patch("/api/auth/user")
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
        User userSameEmail = new User("test2@mail.com", "newPassword", "newName");
        Response response = getRegisterResponse(userSameEmail);
        token2 = response
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

        User userSameEmailPatch = new User("test2@mail.com", password, name);
        Response response2 = getPatchResponse(userSameEmailPatch);
        response2
                .then()
                .statusCode(403)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("User with such email already exists"));

    }

    @Test
    public void verifyUnauthorizedUserCannotPatch() {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
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
        if (token2 != null) {
            given()
                    .header("Authorization", token2)
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
}
