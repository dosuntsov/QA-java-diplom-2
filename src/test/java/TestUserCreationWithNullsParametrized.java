import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserClient;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)

public class TestUserCreationWithNullsParametrized {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    UserClient user;

    public TestUserCreationWithNullsParametrized(String email, String password, String name){
        this.user = new UserClient(email, password, name);
    }

    @Parameterized.Parameters
    public static Object[][] variableNullsInQuery() {
        return new Object[][]{
                {null, "kingofpirates", "Monkey D. Luffy"},
                {"mugiwara@op.com", null, "Monkey D. Luffy"},
                {"mugiwara@op.com", "kingofpirates" , null},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    public void checkIfRegistrationFailsWithNullParams(){
        Response response = user.getRegisterResponse();
        String token = response
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"))
                .extract()
                .path("accessToken");
        user.setToken(token);
    }

    @After
    public void deleteUserAfterTest() {
        user.deleteUser();
    }

}
