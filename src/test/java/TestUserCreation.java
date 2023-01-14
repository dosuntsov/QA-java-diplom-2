import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestUserCreation {
    User user = new User("mugiwara@op.com", "kingofpirates", "Monkey D. Luffy");
    String token;

    public Response createUser(){
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register");
        return response;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }



    @Test
    public void validCreatingANewUser() {
        Response response = createUser();
        token = response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken",notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    public void creatingTheSameUser() {
        Response response = createUser();
        token = response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken",notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");

        Response response2 = createUser();
        response2
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
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
