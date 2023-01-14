import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)

public class TestUserCreationWithNullsParametrized {
    private User user;

    public TestUserCreationWithNullsParametrized(String email, String password, String name){
        this.user = new User(email, password, name);
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
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void checkIfRegistrationFailsWithNullParams(){
        given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
