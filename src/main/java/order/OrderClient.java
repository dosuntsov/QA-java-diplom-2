package order;

import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;


public class OrderClient {
    private static final String GET_ORDERS_URL  = "/api/orders";
    private String token;
    private Ingredients ingredients;

    public OrderClient(List<String> list){
        ingredients = new Ingredients(list);
    }

    public void setToken(String token){
        this.token = token;
    }
    public void setNewList(List<String> list){ingredients = new Ingredients(list);}
    public void setNewList(){ingredients = new Ingredients();}

    public Response getResponseCreateAnOrder() {
        if (token == null) {
            return given()
                    .header("Content-type", "application/json")
                    .body(ingredients)
                    .post(GET_ORDERS_URL);
        }

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(ingredients)
                .post(GET_ORDERS_URL);
    }
    public Response getResponseAnOrder() {
        if(token == null) {
            return given()
                    .header("Content-type", "application/json")
                    .get(GET_ORDERS_URL);
        }
        return  given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .get(GET_ORDERS_URL);
    }
}
