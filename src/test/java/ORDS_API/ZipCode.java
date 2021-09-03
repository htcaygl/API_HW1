package ORDS_API;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigurationReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;
import static org.hamcrest.Matchers.*;


public class ZipCode {

    @BeforeClass
    public void beforeclass(){
        baseURI= ConfigurationReader.get("zipcode_api_url");
    }

    /*Given Accept application/json
And path zipcode is 22031
When I send a GET request to /us endpoint
Then status code must be 200
And content type must be application/json
And Server header is cloudflare
And Report-To header exists
And body should contains following information
    post code is 22031
    country  is United States
    country abbreviation is US
    place name is Fairfax
    state is Virginia
    latitude is 38.8604*/

    @Test
    public void q1(){

        Response response=given().accept(ContentType.JSON)
                .and().pathParam("zipcode","22031")
                .when().get("/us/{zipcode}");


        assertEquals(response.statusCode(),200);
        assertEquals(response.contentType(),"application/json");

        assertEquals(response.header("Server"),"cloudflare");
        assertTrue(response.headers().hasHeaderWithName("Report-To"));


        JsonPath jsonPath=response.jsonPath();

        assertEquals(jsonPath.getString("'post code'"),"22031");    // there is space in path(post code). that's why we need to add ' '
        assertEquals(jsonPath.getString("country"),"United States");
        assertEquals(jsonPath.getString("'country abbreviation'"),"US");
        assertEquals(jsonPath.getString("places.'place name'[0]"), "Fairfax"); // there is space in path. ' '
        assertEquals(jsonPath.getString("places.state[0]"), "Virginia");
        assertEquals(jsonPath.getString("places.latitude[0]"), "38.8604");

    }

    /*Given Accept application/json
And path zipcode is 50000
When I send a GET request to /us endpoint
Then status code must be 404
And content type must be application/json*/

    @Test
    //with Hamcrest Matchers
    public void q2(){

        given().accept(ContentType.JSON)
                .and().pathParam("zipcode",50000)
                .when().get("/us/{zipcode}")
                .then().statusCode(404)
                .and().assertThat().contentType(equalTo("application/json"))
                .log().all();

    }

    /*Given Accept application/json
And path state is va
And path city is fairfax
When I send a GET request to /us endpoint
Then status code must be 200
And content type must be application/json
And payload should contains following information
    country abbreviation is US
    country  United States
    place name  Fairfax
    each places must contains fairfax as a value
    each post code must start with 22
    */

    @Test
    public void q3(){

       Response response = given().accept(ContentType.JSON)
                .and().pathParam("state","va")
                .and().pathParam("city","fairfax")
                .when().get("/us/{state}/{city}");


        assertEquals(response.statusCode(),200);
        assertEquals(response.contentType(),"application/json");


        JsonPath jsonPath=response.jsonPath();

        assertEquals(jsonPath.getString("'country abbreviation'"),"US");
        assertEquals(jsonPath.getString("country"),"United States");
        assertEquals(jsonPath.getString("'place name'"),"Fairfax");

        System.out.println("jsonPath.getList(\"places.'place name'\") = " + jsonPath.getList("places.'place name'"));
        assertTrue(jsonPath.getList("places.'place name'").contains("Fairfax"));

        List<String> postcodes = jsonPath.getList("places.'post code'");

        System.out.println("postcodes = " + postcodes);

        for (String postcode : postcodes) {

            assertTrue(postcode.startsWith("22"));

        }

    }
}
