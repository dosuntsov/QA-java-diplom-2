import io.restassured.RestAssured;
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
    public void verifyChangingPassword(){
        User user1 = new User(email, "newPassword", name);
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
                .body("user.name" ,equalTo(name))
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    public void verifyChangingName(){
        User user2 = new User(email, password, "newName");
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(user2)
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name" ,equalTo("newName"))
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    public void verifyChangingNewEmail(){
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
                .body("user.name" ,equalTo(name))
                .and()
                .body("user.email", equalTo("newemail"));
    }

    @Test
    public void verifyChangingToExistingEmailFails(){
        User userSameEmail = new User("test@mail.com", "newPassword", "newName");
        token2 = given()
                .header("Content-type", "application/json")
                .body(userSameEmail)
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

        User userSameEmailPatch = new User("test@mail.com", password, name);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(userSameEmailPatch)
                .patch("/api/auth/user")
                .then()
                .statusCode(403)
                .and().body("success", equalTo(false))
                .and().body("message",equalTo("User with such email already exists"));

    }
    @Test
    public void verifyUnauthorizedUserCannotPatch(){
        given()
                .header("Content-type", "application/json")
                .body(user)
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .and().body("success", equalTo(false))
                .and().body("message",equalTo("You should be authorised"));
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
        if(token2 != null){
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
