package user;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserClient {
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String AUTH_USER = "/api/auth/user";
    private String token;
    private UserJSON user;

    public UserClient(String email, String password, String name) {
        user = new UserJSON(email, password, name);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void deleteUser() {
        if (token != null) {
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

    public Response getRegisterResponse() {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(REGISTER_URL);
    }

    public Response getLoginResponse() {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_URL);
    }

    public Response getPatchResponse(String email, String password, String name) {
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        if (token == null) {
            return given()
                    .header("Content-type", "application/json")
                    .body(user)
                    .patch(AUTH_USER);
        }
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(user)
                .patch(AUTH_USER);
    }
}
