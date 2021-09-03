package ORDS_API;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigurationReader;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;
import static io.restassured.RestAssured.*;

public class ORDS {

    @BeforeClass
    public void beforeclass(){
        baseURI= ConfigurationReader.get("hr_api_url");
    }

    /*Q1:
- Given accept type is Json
- Path param value- US
- When users sends request to /countries
- Then status code is 200
- And Content - Type is Json
- And country_id is US
- And Country_name is United States of America
- And Region_id is*/

    @Test
    public void Q1(){

        // Using with JsonPath
        Response response = given().accept(ContentType.JSON)
                .and().pathParam("id", "US")
                .when().get("/countries/{id}");

        assertEquals(response.statusCode(),200);
        assertEquals(response.contentType(),"application/json");

        JsonPath jsonPath=response.jsonPath();

        assertEquals(jsonPath.getString("country_id"),"US");
        assertEquals(jsonPath.getString("country_name"),"United States of America");
        assertEquals(jsonPath.getInt("region_id"),2);

    }
    /*Q2:
- Given accept type is Json
- Query param value - q={"department_id":80}
- When users sends request to /employees
- Then status code is 200
- And Content - Type is Json
- And all job_ids start with 'SA'
- And all department_ids are 80
- Count is 25*/

    @Test
    public void Q2(){
        Response response = given().accept(ContentType.JSON)
                .and().queryParam("q", "{\"department_id\":80}")
                .when().get("/employees");

        assertEquals(response.statusCode(),200);
        assertEquals(response.contentType(),"application/json");


        JsonPath jsonPath=response.jsonPath();

        List<String> job_ids=jsonPath.getList("items.job_id");

        //verify all job_ids start with 'SA'
        for (String job_id:job_ids) {
            assertTrue(job_id.startsWith("SA"));
        }


        //verify all department_ids are 80
        List<Integer> department_ids = jsonPath.getList("items.department_id");
        for (int department_id:department_ids) {
            assertTrue(department_id==80);
        }

        //verify Count is 25
        assertEquals(jsonPath.getInt("count"),25);

    }

    /*Q3:
- Given accept type is Json
-Query param value q= region_id 3
- When users sends request to /countries
- Then status code is 200
- And all regions_id is 3
- And count is 6
- And hasMore is false
- And Country_name are;
Australia,China,India,Japan,Malaysia,Singapore*/

    @Test
    public void Q3(){

        Response response = given().accept(ContentType.JSON)
                .and().queryParam("q", "{\"region_id\":3}")
                .when().get("/countries");


        assertEquals(response.statusCode(),200);

        JsonPath jsonPath=response.jsonPath();

        List<Integer> region_ids = jsonPath.getList("items.region_id");

        for (int region_id : region_ids) {
            assertEquals(region_id,3);
        }

        assertEquals(jsonPath.getInt("count"),6);
        assertEquals(jsonPath.getBoolean("hasMore"),false);

        //actual list
        List<String> country_names = jsonPath.getList("items.country_name");
        System.out.println("country_names = " + country_names);


        //expected list
        List<String> expectedCountries=new ArrayList<>();
        expectedCountries.addAll(Arrays.asList("Australia", "China", "India", "Japan", "Malaysia", "Singapore"));

        System.out.println("expectedCountries = " + expectedCountries);

        assertEquals(country_names,expectedCountries);
    }

}
